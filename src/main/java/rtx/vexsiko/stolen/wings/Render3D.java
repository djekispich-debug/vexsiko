package rtx.vexsiko.stolen.wings;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import rtx.vexsiko.IMinecraft;

/**
 * Kimiko adaptation of dile.ru.utils.render.Render3D
 * Minimal stub providing fields required by Wings and shader renderer.
 * Full line/quad queues kept as no-ops for compilation; original logic preserved as reference.
 */
public final class Render3D implements IMinecraft {

    public static final Matrix4f lastProjMat = new Matrix4f();
    public static final Matrix4f lastModMat = new Matrix4f();
    public static final Matrix4f lastWorldSpaceMatrix = new Matrix4f();

    public static MatrixStack.Entry lastWorldSpaceEntry = new MatrixStack().peek();
    public static float lastTickDelta = 1.0f;
    public static Vec3d lastCameraPos = Vec3d.ZERO;
    public static Quaternionf lastCameraRotation = new Quaternionf();

    private Render3D() {}

    public static void capture(Matrix4fc projection, Matrix4fc view, Vec3d cameraPos) {
        if (projection != null) lastProjMat.set(projection);
        if (view != null) {
            lastModMat.set(view);
            lastWorldSpaceMatrix.set(view);
        }
        if (cameraPos != null) lastCameraPos = cameraPos;
    }

    public static void setLastWorldSpaceEntry(MatrixStack.Entry entry) {
        if (entry != null) lastWorldSpaceEntry = entry;
    }

    public static void setLastTickDelta(float tickDelta) {
        lastTickDelta = Float.isFinite(tickDelta) ? tickDelta : 1.0f;
    }

    public static void setLastCameraPos(Vec3d cameraPos) {
        if (cameraPos != null) lastCameraPos = cameraPos;
    }

    public static void setLastCameraRotation(Quaternionf rotation) {
        if (rotation != null) lastCameraRotation = rotation;
    }

    // No-op queue methods to keep original API surface compiling
    public static void drawLine(Vec3d start, Vec3d end, int color, float width, boolean depth) {}
    public static void drawQuad(Vec3d a, Vec3d b, Vec3d c, Vec3d d, int color, boolean depth) {}
    public static void drawBox(net.minecraft.util.math.Box box, int color, float width, boolean line, boolean fill, boolean depth) {}
    public static void clearQueues() {}
}
