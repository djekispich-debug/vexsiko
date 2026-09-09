#version 150

in vec2 noiseCoord;
in vec2 texCoord;
in vec3 dimColor;
in float dimAlpha;
in float reveal;
in float mode;
in float softness;

out vec4 fragColor;

uniform sampler2D Sampler0;  // снимок кадра: фон при проявлении, само окно при разъедании
uniform sampler2D Sampler1;  // текстура шума

/**
 * Значение маски в точке. Берём минимум альфы и яркости: у текстур с шумом
 * в альфе это сама альфа, у чёрно-белых без альфа-канала (там она всегда 1.0)
 * — яркость.
 */
float sampleNoise(vec2 uv) {
    vec4 texel = texture(Sampler1, fract(uv));
    return min(texel.a, dot(texel.rgb, vec3(0.299, 0.587, 0.114)));
}

/**
 * Порог растворения работает по градиенту значений. Если текстура нарисована
 * жёстко (только 0 и 1), промежуточных значений нет и окно не проявляется, а
 * выскакивает целиком. Размытие по девяти отсчётам делает из такой картинки
 * плавную маску, сохраняя форму пятен.
 */
float maskValue() {
    if (softness <= 0.0) return sampleNoise(noiseCoord);

    float r = softness;
    float sum = sampleNoise(noiseCoord) * 4.0;

    sum += sampleNoise(noiseCoord + vec2( r, 0.0)) * 2.0;
    sum += sampleNoise(noiseCoord + vec2(-r, 0.0)) * 2.0;
    sum += sampleNoise(noiseCoord + vec2(0.0,  r)) * 2.0;
    sum += sampleNoise(noiseCoord + vec2(0.0, -r)) * 2.0;

    sum += sampleNoise(noiseCoord + vec2( r,  r));
    sum += sampleNoise(noiseCoord + vec2( r, -r));
    sum += sampleNoise(noiseCoord + vec2(-r,  r));
    sum += sampleNoise(noiseCoord + vec2(-r, -r));

    return sum / 16.0;
}

void main() {
    // оригинал пишет стенсил там, где значение шума > порога, и рисует GUI по
    // нулям стенсила — то есть видно ровно там, где значение <= порога
    bool hidden = maskValue() > reveal;

    if (mode < 0.5) {
        // проявление: возвращаем фон в скрытых ячейках
        if (!hidden) discard;
    } else {
        // разъедание: дорисовываем сохранённое окно в ещё видимых ячейках
        if (hidden) discard;
    }

    vec3 color = texture(Sampler0, texCoord).rgb;
    fragColor = vec4(mix(color, dimColor, dimAlpha), 1.0);
}
