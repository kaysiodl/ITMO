import math


def is_equidistant(xs):
    if len(xs) < 2:
        return True
    h = xs[1] - xs[0]
    return all(abs((xs[i + 1] - xs[i]) - h) < 1e-9 for i in range(len(xs) - 1))


def finite_differences(ys):
    table = [list(ys)]
    for k in range(1, len(ys)):
        prev = table[k - 1]
        table.append([prev[i + 1] - prev[i] for i in range(len(prev) - 1)])
    return table


def divided_differences(xs, ys):
    n = len(xs)
    table = [list(ys)]
    for k in range(1, n):
        prev = table[k - 1]
        table.append([(prev[i + 1] - prev[i]) / (xs[i + k] - xs[i]) for i in range(len(prev) - 1)])
    return table


def lagrange(xs, ys, x):
    n = len(xs)
    result = 0.0
    for i in range(n):
        li = 1.0
        for j in range(n):
            if i != j:
                li *= (x - xs[j]) / (xs[i] - xs[j])
        result += ys[i] * li
    return result


def newton_divided_forward(xs, ys, x):
    dd = divided_differences(xs, ys)
    n = len(xs)
    result = dd[0][0]
    prod = 1.0
    for k in range(1, n):
        prod *= x - xs[k - 1]
        result += dd[k][0] * prod
    return result


def newton_divided_backward(xs, ys, x):
    dd = divided_differences(xs, ys)
    n = len(xs)
    result = dd[0][n - 1]
    prod = 1.0
    for k in range(1, n):
        prod *= x - xs[n - k]
        result += dd[k][n - 1 - k] * prod
    return result


def newton_finite_forward(xs, ys, x):
    n = len(xs)
    h = xs[1] - xs[0]
    t = (x - xs[0]) / h
    diff = finite_differences(ys)
    result = ys[0]
    prod = 1.0
    for k in range(1, n):
        prod *= t - (k - 1)
        result += (prod / math.factorial(k)) * diff[k][0]
    return result


def newton_finite_backward(xs, ys, x):
    n = len(xs)
    h = xs[1] - xs[0]
    t = (x - xs[-1]) / h
    diff = finite_differences(ys)
    result = ys[-1]
    prod = 1.0
    for k in range(1, n):
        prod *= t + (k - 1)
        result += (prod / math.factorial(k)) * diff[k][n - 1 - k]
    return result


def _nearest_left(xs, x):
    m = 0
    for i in range(len(xs)):
        if xs[i] <= x:
            m = i
    return m


def _nearest(xs, x):
    m = 0
    best = abs(x - xs[0])
    for i in range(1, len(xs)):
        d = abs(x - xs[i])
        if d < best:
            best = d
            m = i
    return m


def gauss_forward(xs, ys, x):
    n = len(xs)
    h = xs[1] - xs[0]
    diff = finite_differences(ys)
    m = (n - 1) // 2
    t = (x - xs[m]) / h
    result = ys[m]
    for k in range(1, n):
        idx = m - k // 2
        if idx < 0 or idx >= len(diff[k]):
            break
        num = 1.0
        for j in range(-(k // 2), (k - 1) // 2 + 1):
            num *= t + j
        result += (num / math.factorial(k)) * diff[k][idx]
    return result


def gauss_backward(xs, ys, x):
    n = len(xs)
    h = xs[1] - xs[0]
    diff = finite_differences(ys)
    m = (n - 1) // 2
    t = (x - xs[m]) / h
    result = ys[m]
    for k in range(1, n):
        idx = m - (k + 1) // 2
        if idx < 0 or idx >= len(diff[k]):
            break
        num = 1.0
        for j in range(-((k - 1) // 2), k // 2 + 1):
            num *= t + j
        result += (num / math.factorial(k)) * diff[k][idx]
    return result


def stirling(xs, ys, x):
    n = len(xs)
    h = xs[1] - xs[0]
    diff = finite_differences(ys)
    m = (n - 1) // 2
    t = (x - xs[m]) / h
    t2 = t * t
    result = ys[m]
    for j in range(1, n):
        k_odd = 2 * j - 1
        k_even = 2 * j
        if k_odd >= n:
            break
        if k_odd < len(diff):
            i1, i2 = m - j, m - j + 1
            if i1 >= 0 and i2 < len(diff[k_odd]):
                num = t
                for i in range(1, j):
                    num *= t2 - i * i
                avg = (diff[k_odd][i1] + diff[k_odd][i2]) / 2.0
                result += (num / math.factorial(k_odd)) * avg
        if k_even < n and k_even < len(diff):
            idx = m - j
            if 0 <= idx < len(diff[k_even]):
                num = t2
                for i in range(1, j):
                    num *= t2 - i * i
                result += (num / math.factorial(k_even)) * diff[k_even][idx]
    return result


def bessel(xs, ys, x):
    n = len(xs)
    h = xs[1] - xs[0]
    diff = finite_differences(ys)
    m = n // 2 - 1
    if m < 0:
        m = 0
    if m >= n - 1:
        m = n - 2
    t = (x - xs[m]) / h
    result = (ys[m] + ys[m + 1]) / 2.0
    result += (t - 0.5) * diff[1][m]
    for j in range(1, n // 2 + 1):
        k_even = 2 * j
        k_odd = 2 * j + 1
        if k_even < n and k_even < len(diff):
            num = 1.0
            for i in range(j):
                num *= (t + i) * (t - i - 1)
            i1, i2 = m - j, m - j + 1
            if i1 >= 0 and i2 < len(diff[k_even]):
                avg = (diff[k_even][i1] + diff[k_even][i2]) / 2.0
                result += (num / math.factorial(k_even)) * avg
        if k_odd < n and k_odd < len(diff):
            num = t - 0.5
            for i in range(j):
                num *= (t + i) * (t - i - 1)
            idx = m - j
            if 0 <= idx < len(diff[k_odd]):
                result += (num / math.factorial(k_odd)) * diff[k_odd][idx]
    return result
