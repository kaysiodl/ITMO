function f1(x) { return x**3 + 2.28*x**2 - 1.934*x - 3.907; }
function f2(x) { return Math.sin(x) - x/2; }
function f3(x) { return x**2 - 4; }

function validateInputs(funcName, a, b, eps) {
    if (isNaN(a) || isNaN(b) || isNaN(eps)) {
        return "Введите все значения";
    }
    if (eps <= 0) {
        return "eps должен быть > 0";
    }
    if (eps < 1e-12) {
        return "eps слишком маленький";
    }
    if (a >= b) {
        return "Должно быть: a < b";
    }
    return null;
}

function showError(message, errorDiv) {
    const errDiv = document.getElementById(errorDiv);
    console.log(errorDiv, message);
    if (message) {
        errDiv.innerText = message;
        errDiv.style.display = "block";
    } else {
        errDiv.style.display = "none";
    }
}

document.getElementById("singleModeBtn").addEventListener("click", () => {
    document.getElementById("singlePanel").style.display = "flex";
    document.getElementById("systemPanel").style.display = "none";
    document.getElementById("singleModeBtn").classList.add("active");
    document.getElementById("systemModeBtn").classList.remove("active");
    Plotly.purge("plot");
});

document.getElementById("systemModeBtn").addEventListener("click", () => {
    document.getElementById("singlePanel").style.display = "none";
    document.getElementById("systemPanel").style.display = "flex";
    document.getElementById("systemModeBtn").classList.add("active");
    document.getElementById("singleModeBtn").classList.remove("active");
    Plotly.purge("plot");
});

document.getElementById("solveBtn").addEventListener("click", async () => {
    showError(null, "errorMessage2");
    const funcName = document.getElementById("funcSelect").value;
    const method = document.getElementById("methodSelect").value;
    const a = parseFloat(document.getElementById("a").value);
    const b = parseFloat(document.getElementById("b").value);
    const eps = parseFloat(document.getElementById("eps").value);

    const error = validateInputs(funcName, a, b, eps);
    if (error) {
        showError(error, "errorMessage2");
        return;
    }

    let func = funcName === "f1" ? f1 : funcName === "f2" ? f2 : f3;
    const xs = [];
    const ys = [];
    const left = a - 1;
    const right = b + 1;

    for(let i = left; i <= right; i += 0.01){
        xs.push(i);
        ys.push(func(i));
    }

    const baseLayout = {
        title: 'График функции',
        paper_bgcolor: 'rgba(0,0,0,0)',
        plot_bgcolor: 'rgba(0,0,0,0)',
        font: { color: '#f8fafc' },
        shapes: [
            { type: 'line', x0: a, x1: a, y0: -10, y1: 10, line: { color: 'green', width: 2, dash: 'dash' } },
            { type: 'line', x0: b, x1: b, y0: -10, y1: 10, line: { color: 'blue', width: 2, dash: 'dash' } }
        ],
        xaxis: { title: 'x', range: [left, right], gridcolor: '#334155', zerolinecolor: '#64748b' },
        yaxis: { title: 'f(x)', range: [-10, 10], gridcolor: '#334155', zerolinecolor: '#64748b' }
    };

    const dataToPlot = [{ x: xs, y: ys, mode: 'lines', name: 'f(x)' }];
    Plotly.newPlot('plot', dataToPlot, baseLayout);

    try {
        const response = await fetch("http://localhost:8000/solve", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ function: funcName, method: method, a: a, b: b, eps: eps })
        });

        const result = await response.json();

        if (!response.ok) {
            showError("Ошибка: " + (result.detail || result.message || "Неизвестная ошибка"), "errorMessage2");
            return;
        }
        document.getElementById("xVal").innerText = "x = " + result.x.toFixed(8);
        document.getElementById("fxVal").innerText = "f(x) = " + result.fx.toFixed(8);
        document.getElementById("iterVal").innerText = "Итерации = " + result.iterations;

        Plotly.addTraces('plot', {
            x: [result.x],
            y: [result.fx],
            mode: 'markers',
            marker: { color: 'red', size: 7 },
            name: 'Корень'
        });
    } catch (e) {
        alert("Ошибка соединения с сервером");
    }
});

document.getElementById("solveSysBtn").addEventListener("click", async () => {
    showError(null, "errorMessage1");
    const system = document.getElementById("sysSelect").value;
    const x_0 = parseFloat(document.getElementById("x_0").value);
    const y_0 = parseFloat(document.getElementById("y_0").value);
    const eps = parseFloat(document.getElementById("sysEps").value);

    if (isNaN(x_0) || isNaN(y_0) || isNaN(eps)) {
        showError("Введите все значения", "errorMessage1");
        return;
    }

    if (eps < 0){
        showError("Эпсилон не может быть отрицательным", "errorMessage1");
        return;
    }

    const response = await fetch("http://localhost:8000/solve-system", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ system: system, x_0: x_0, y_0: y_0, eps: eps })
    });

    const result = await response.json();

    if (!response.ok) {
        showError("Ошибка сервера: " + (result.detail || result.message || "Неизвестная ошибка"), "errorMessage1");
        return;
    }

    document.getElementById("sysX1").innerText    = "x = " + result.x.toFixed(6);
    document.getElementById("sysX2").innerText    = "y = " + result.y.toFixed(6);
    document.getElementById("sysIter").innerText  = "Итерации = " + result.iterations;
    document.getElementById("sysErr1").innerText  = "|x - x_prev| = " + result.err1.toExponential(2);
    document.getElementById("sysErr2").innerText  = "|y - y_prev| = " + result.err2.toExponential(2);

    const plotData = [];
    const rangeX = [];
    for (let x = -4; x <= 4; x += 0.05) rangeX.push(x);

    if (system === "sys1") { // x - cos(y) = 0.5; y - sin(x) = -0.3
        const line1 = { x: [], y: [], name: 'x = cos(y) + 0.5', mode: 'lines', line: {color: 'blue'} };
        const line2 = { x: [], y: [], name: 'y = sin(x) - 0.3', mode: 'lines', line: {color: 'green'} };
        rangeX.forEach(v => {
            line2.x.push(v);
            line2.y.push(Math.sin(v) - 0.3);
            line1.y.push(v);
            line1.x.push(Math.cos(v) + 0.5);
        });
        plotData.push(line1, line2);
    } else { // x^2 + y = 2; x + y^2 = 2
        const line1 = { x: [], y: [], name: 'y = 2 - x^2', mode: 'lines', line: {color: 'blue'} };
        const line2 = { x: [], y: [], name: 'x + y^2 = 2', mode: 'lines', line: {color: 'green'} };
        rangeX.forEach(v => {
            line1.x.push(v);
            line1.y.push(2 - Math.pow(v, 2));
            line2.y.push(v);
            line2.x.push(2 - Math.pow(v, 2));
        });
        plotData.push(line1, line2);
    }

    plotData.push({
        x: [result.x],
        y: [result.y],
        mode: 'markers',
        marker: { size: 12, color: 'red' },
        name: 'Решение'
    });

    const layout = {
        title: "Графическое решение системы",
        paper_bgcolor: 'rgba(0,0,0,0)',
        plot_bgcolor: 'rgba(0,0,0,0)',
        font: { color: '#f8fafc' },
        xaxis: {
            title: 'x',
            range: [-3, 3],
            gridcolor: '#334155',
            zerolinecolor: '#64748b'
        },
        yaxis: {
            title: 'f(x)',
            range: [-3, 3],
            gridcolor: '#334155',
            zerolinecolor: '#64748b'
        },
        showlegend: true
    };

    Plotly.newPlot('plot', plotData, layout);
});