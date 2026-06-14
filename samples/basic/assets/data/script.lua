local time = 0.0

function render(delta)
    time = time + delta
    local red = 0.12 + (math.sin(time * 0.90) + 1.0) * 0.20
    local green = 0.16 + (math.sin(time * 1.30 + 1.2) + 1.0) * 0.18
    local blue = 0.24 + (math.sin(time * 1.70 + 2.4) + 1.0) * 0.22
    host_clear(red, green, blue, 1.0)
end
