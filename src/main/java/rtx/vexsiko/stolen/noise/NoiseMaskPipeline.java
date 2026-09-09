package rtx.vexsiko.stolen.noise;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GpuSampler;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gl.UniformType;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.OptionalDouble;
import java.util.OptionalInt;

public class NoiseMaskPipeline {
    private static final Identifier PIPELINE_ID = Identifier.of("vexsiko", "pipeline/noise_mask");
    private static final Identifier SHADER_ID = Identifier.of("vexsiko", "core/noise_mask");
    private static final Vector3f MODEL_OFFSET = new Vector3f(0, 0, 0);
    private static final Matrix4f TEXTURE_MATRIX = new Matrix4f();
    private static final Vector4f COLOR_MODULATOR = new Vector4f(1f, 1f, 1f, 1f);
    private static final int BUFFER_SIZE = 80;

    private static final RenderPipeline PIPELINE = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET)
                    .withLocation(PIPELINE_ID)
                    .withVertexShader(SHADER_ID)
                    .withFragmentShader(SHADER_ID)
                    .withVertexFormat(VertexFormats.EMPTY, VertexFormat.DrawMode.TRIANGLES)
                    .withUniform("NoiseMaskData", UniformType.UNIFORM_BUFFER)
                    .withSampler("Sampler0")
                    .withSampler("Sampler1")
                    .withBlend(BlendFunction.TRANSLUCENT)
                    .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                    .withDepthWrite(false)
                    .withCull(false)
                    .build()
    );

    private static final NoiseMaskPipeline INSTANCE = new NoiseMaskPipeline();

    public static NoiseMaskPipeline getInstance() {
        return INSTANCE;
    }

    private GpuBuffer uniformBuffer;
    private GpuBuffer dummyVertexBuffer;
    private ByteBuffer dataBuffer;
    private boolean initialized;

    private GpuTexture backdrop;
    private GpuTextureView backdropView;
    private GpuTexture gui;
    private GpuTextureView guiView;
    private int snapshotWidth;
    private int snapshotHeight;
    private boolean backdropValid;
    private boolean guiValid;

    private void ensureInitialized() {
        if (initialized) return;

        dataBuffer = MemoryUtil.memAlloc(BUFFER_SIZE);
        ByteBuffer dummyData = MemoryUtil.memAlloc(4);
        dummyData.putInt(0);
        dummyData.flip();
        dummyVertexBuffer = RenderSystem.getDevice().createBuffer(
                () -> "vexsiko:noise_mask_dummy_vertex",
                GpuBuffer.USAGE_VERTEX,
                dummyData
        );
        MemoryUtil.memFree(dummyData);
        initialized = true;
    }

    public void captureBackdrop() {
        backdropValid = copyFrame(true);
    }

    public void captureGui() {
        guiValid = copyFrame(false);
    }

    private boolean copyFrame(boolean toBackdrop) {
        MinecraftClient client = MinecraftClient.getInstance();
        Framebuffer fb = client.getFramebuffer();
        if (fb == null) return false;

        GpuTexture source = fb.getColorAttachment();
        if (source == null || source.isClosed()) return false;

        int w = fb.textureWidth;
        int h = fb.textureHeight;
        if (w <= 0 || h <= 0) return false;

        ensureInitialized();
        ensureSnapshot(w, h);

        GpuTexture target = toBackdrop ? backdrop : gui;
        if (target == null) return false;

        // Original called DrawBatcher.flushPending(); vexsiko has no global batcher here - render is immediate via GuiRenderer pipelines.
        // No-op: most batched 2D is already flushed via GuiRenderer lifecycle; copyTextureToTexture is safe.

        RenderSystem.getDevice().createCommandEncoder()
                .copyTextureToTexture(source, target, 0, 0, 0, 0, 0, w, h);
        return true;
    }

    private void ensureSnapshot(int width, int height) {
        if (backdrop != null && gui != null && width == snapshotWidth && height == snapshotHeight) return;

        closeSnapshot();
        backdrop = createSnapshotTexture("vexsiko:noise_mask_backdrop", width, height);
        backdropView = RenderSystem.getDevice().createTextureView(backdrop);
        gui = createSnapshotTexture("vexsiko:noise_mask_gui", width, height);
        guiView = RenderSystem.getDevice().createTextureView(gui);
        snapshotWidth = width;
        snapshotHeight = height;
    }

    private GpuTexture createSnapshotTexture(String name, int width, int height) {
        return RenderSystem.getDevice().createTexture(
                () -> name,
                GpuTexture.USAGE_COPY_DST | GpuTexture.USAGE_TEXTURE_BINDING | GpuTexture.USAGE_RENDER_ATTACHMENT,
                TextureFormat.RGBA8,
                width, height, 1, 1
        );
    }

    public boolean hasBackdrop() {
        return backdropValid && backdropView != null && !backdropView.isClosed();
    }

    public boolean hasGui() {
        return guiValid && guiView != null && !guiView.isClosed();
    }

    public void invalidateGui() {
        guiValid = false;
    }

    public void draw(Identifier noiseTexture,
                     float x, float y, float width, float height,
                     float reveal, float tiling,
                     int tintColor, float tintStrength, boolean erase, float softness) {
        MinecraftClient client = MinecraftClient.getInstance();
        Framebuffer fb = client.getFramebuffer();
        if (fb == null || width <= 0f || height <= 0f) return;
        if (erase ? !hasGui() : !hasBackdrop()) return;

        AbstractTexture noise = client.getTextureManager().getTexture(noiseTexture);
        if (noise == null || noise.getGlTextureView() == null) return;

        ensureInitialized();

        float screenWidth = client.getWindow().getScaledWidth();
        float screenHeight = client.getWindow().getScaledHeight();
        if (screenWidth <= 0f || screenHeight <= 0f) return;

        float guiScale = fb.textureWidth / screenWidth;

        dataBuffer.clear();

        dataBuffer.putFloat(x);
        dataBuffer.putFloat(y);
        dataBuffer.putFloat(width);
        dataBuffer.putFloat(height);

        dataBuffer.putFloat(screenWidth);
        dataBuffer.putFloat(screenHeight);
        dataBuffer.putFloat(guiScale);
        dataBuffer.putFloat(reveal);

        dataBuffer.putFloat(fb.textureWidth);
        dataBuffer.putFloat(fb.textureHeight);
        dataBuffer.putFloat(tiling);
        dataBuffer.putFloat(erase ? 1f : 0f);

        dataBuffer.putFloat(((tintColor >> 16) & 0xFF) / 255.0f);
        dataBuffer.putFloat(((tintColor >> 8) & 0xFF) / 255.0f);
        dataBuffer.putFloat((tintColor & 0xFF) / 255.0f);
        dataBuffer.putFloat(tintStrength);

        dataBuffer.putFloat(softness);
        dataBuffer.putFloat(0f);
        dataBuffer.putFloat(0f);
        dataBuffer.putFloat(0f);

        dataBuffer.flip();
        uploadAndDraw(client, noise.getGlTextureView(), erase ? guiView : backdropView);
    }

    private void uploadAndDraw(MinecraftClient client, GpuTextureView noiseView, GpuTextureView sourceView) {
        // No pending DrawBatcher in vexsiko; immediate mode equivalent.

        int size = dataBuffer.remaining();
        if (uniformBuffer == null || uniformBuffer.size() < size) {
            if (uniformBuffer != null) uniformBuffer.close();
            uniformBuffer = RenderSystem.getDevice().createBuffer(
                    () -> "vexsiko:noise_mask_uniform",
                    GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_COPY_DST,
                    size
            );
        }

        CommandEncoder encoder = RenderSystem.getDevice().createCommandEncoder();
        encoder.writeToBuffer(uniformBuffer.slice(), dataBuffer);

        GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms()
                .write(RenderSystem.getModelViewMatrix(), COLOR_MODULATOR, MODEL_OFFSET, TEXTURE_MATRIX);
        GpuSampler sampler = RenderSystem.getSamplerCache().get(FilterMode.LINEAR);

        try (RenderPass renderPass = encoder.createRenderPass(
                () -> "vexsiko:noise_mask_pass",
                client.getFramebuffer().getColorAttachmentView(),
                OptionalInt.empty(),
                client.getFramebuffer().getDepthAttachmentView(),
                OptionalDouble.empty())) {

            renderPass.setPipeline(PIPELINE);
            renderPass.setVertexBuffer(0, dummyVertexBuffer);
            renderPass.bindTexture("Sampler0", sourceView, sampler);
            renderPass.bindTexture("Sampler1", noiseView, sampler);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", dynamicTransforms);
            renderPass.setUniform("NoiseMaskData", uniformBuffer);
            renderPass.draw(0, 6);
        }
    }

    private void closeSnapshot() {
        if (backdropView != null) { backdropView.close(); backdropView = null; }
        if (backdrop != null)     { backdrop.close();     backdrop = null;     }
        if (guiView != null)      { guiView.close();      guiView = null;      }
        if (gui != null)          { gui.close();          gui = null;          }
        backdropValid = false;
        guiValid = false;
        snapshotWidth = 0;
        snapshotHeight = 0;
    }

    public void close() {
        closeSnapshot();
        if (uniformBuffer != null) {
            uniformBuffer.close();
            uniformBuffer = null;
        }
        if (dummyVertexBuffer != null) {
            dummyVertexBuffer.close();
            dummyVertexBuffer = null;
        }
        if (dataBuffer != null) {
            MemoryUtil.memFree(dataBuffer);
            dataBuffer = null;
        }
        initialized = false;
    }
}
