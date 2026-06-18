<script>
  import { tick, onMount } from 'svelte';
  import { Chart, registerables } from 'chart.js';
  import zoomPlugin from 'chartjs-plugin-zoom';
  import '../src/app.css';

  Chart.register(...registerables, zoomPlugin);

  let equations = [];
  let equation = '';

  let x0 = '0';
  let y0 = '1';
  let xn = '1';
  let h = '0.1';
  let eps = '0.0001';

  let methods = {
    euler: true,
    improved_euler: true,
    milne: true,
  };

  const METHOD_LABELS = {
    euler: 'Метод Эйлера',
    improved_euler: 'Усов. метод Эйлера',
    milne: 'Метод Милна',
  };

  const METHOD_ORDER = ['euler', 'improved_euler', 'milne'];

  const CHART_COLORS = {
    euler: '#ff7b72',
    improved_euler: '#ffa657',
    milne: '#3fb950',
  };

  let response = null;
  let error = null;
  let loading = false;

  let chartCanvas;
  let chartInst = null;

  onMount(async () => {
    try {
      const res = await fetch('/api/equations');
      equations = await res.json();
      if (equations.length) equation = equations[0].key;
    } catch {
      error = 'Не удалось загрузить список уравнений (сервер не запущен?)';
    }
  });

  function selectedMethods() {
    return METHOD_ORDER.filter(k => methods[k]);
  }

  function toggleMethod(key) {
    methods[key] = !methods[key];
    methods = { ...methods };
  }

  async function solve() {
    error = null;
    const sel = selectedMethods();
    if (!sel.length) { error = 'Выберите хотя бы один метод'; return; }

    const payload = {
      equation,
      x0: parseFloat(String(x0).replace(',', '.')),
      y0: parseFloat(String(y0).replace(',', '.')),
      xn: parseFloat(String(xn).replace(',', '.')),
      h: parseFloat(String(h).replace(',', '.')),
      eps: parseFloat(String(eps).replace(',', '.')),
      methods: sel,
    };
    if (Object.values(payload).some(v => typeof v === 'number' && isNaN(v))) {
      error = 'Заполните все числовые поля корректно'; return;
    }

    loading = true;
    try {
      const res = await fetch('/api/solve', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      });
      const data = await res.json();
      if (data.error) { error = data.error; response = null; loading = false; return; }
      response = data;
      await tick();
      renderChart(data);
    } catch {
      error = 'Ошибка соединения с сервером';
    }
    loading = false;
  }

  function renderChart(data) {
    if (chartInst) { chartInst.destroy(); chartInst = null; }
    const datasets = [];

    datasets.push({
      label: 'Точное решение',
      data: data.chart.xs_dense.map((x, i) => ({ x, y: data.chart.exact_dense[i] })),
      type: 'line',
      borderColor: '#58a6ff',
      backgroundColor: 'transparent',
      borderWidth: 2.5,
      pointRadius: 0,
      tension: 0.1,
      order: 1,
    });

    for (const key of METHOD_ORDER) {
      const m = data.methods[key];
      if (!m || !m.ys) continue;
      datasets.push({
        label: METHOD_LABELS[key],
        data: data.xs.map((x, i) => ({ x, y: m.ys[i] })),
        type: 'line',
        borderColor: CHART_COLORS[key],
        backgroundColor: CHART_COLORS[key],
        borderWidth: 1.5,
        borderDash: [5, 4],
        pointRadius: 3,
        tension: 0,
        order: 2,
      });
    }

    Chart.defaults.color = '#8b949e';
    Chart.defaults.borderColor = '#21262d';

    chartInst = new Chart(chartCanvas, {
      type: 'scatter',
      data: { datasets },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        animation: { duration: 250 },
        plugins: {
          legend: { position: 'top', labels: { font: { size: 11 }, boxWidth: 14, color: '#8b949e' } },
          tooltip: {
            backgroundColor: '#161b22', borderColor: '#30363d', borderWidth: 1,
            titleColor: '#79c0ff', bodyColor: '#c9d1d9',
            callbacks: { label: ctx => `${ctx.dataset.label}: (${ctx.parsed.x.toFixed(4)}, ${ctx.parsed.y.toFixed(6)})` },
          },
          zoom: {
            pan: { enabled: true, mode: 'xy' },
            zoom: { wheel: { enabled: true }, pinch: { enabled: true }, mode: 'xy' },
          },
        },
        scales: {
          x: { grid: { color: '#21262d' }, ticks: { color: '#8b949e' },
               title: { display: true, text: 'x', color: '#79c0ff', font: { weight: 'bold' } } },
          y: { grid: { color: '#21262d' }, ticks: { color: '#8b949e' },
               title: { display: true, text: 'y', color: '#79c0ff', font: { weight: 'bold' } } },
        },
      },
    });
  }

  $: activeMethods = response ? METHOD_ORDER.filter(k => response.methods[k]) : [];
</script>

<main>
  <!-- Уравнение -->
  <div class="card">
    <h2>1. Дифференциальное уравнение</h2>
    <div class="form-group">
      <label>Выберите уравнение y′ = f(x, y)</label>
      <select bind:value={equation}>
        {#each equations as eq}
          <option value={eq.key}>{eq.label}</option>
        {/each}
      </select>
    </div>
  </div>

  <!-- Параметры -->
  <div class="card">
    <h2>2. Исходные данные</h2>
    <div class="params-grid">
      <div class="form-group" style="margin-bottom:0">
        <label>x₀</label>
        <input type="text" bind:value={x0} />
      </div>
      <div class="form-group" style="margin-bottom:0">
        <label>y₀ = y(x₀)</label>
        <input type="text" bind:value={y0} />
      </div>
      <div class="form-group" style="margin-bottom:0">
        <label>xₙ</label>
        <input type="text" bind:value={xn} />
      </div>
      <div class="form-group" style="margin-bottom:0">
        <label>Шаг h</label>
        <input type="text" bind:value={h} />
      </div>
      <div class="form-group" style="margin-bottom:0">
        <label>Точность ε</label>
        <input type="text" bind:value={eps} />
      </div>
    </div>
  </div>

  <!-- Методы -->
  <div class="card">
    <h2>3. Методы решения</h2>
    <div class="methods-grid">
      {#each METHOD_ORDER as key}
        <label class="method-check" class:checked={methods[key]}>
          <input type="checkbox" checked={methods[key]} on:change={() => toggleMethod(key)} />
          {METHOD_LABELS[key]}
          <span style="font-size:0.7rem;color:#6e7681;margin-left:auto">
            {key === 'milne' ? 'многошаговый' : 'одношаговый'}
          </span>
        </label>
      {/each}
    </div>
    <p class="info">Одношаговые методы — оценка по правилу Рунге; метод Милна — оценка по точному решению.</p>
    <div style="margin-top:14px">
      <button class="btn btn-primary" on:click={solve} disabled={loading}>
        {loading ? 'Вычисление…' : '▶ Решить'}
      </button>
    </div>
    {#if error}
      <div class="error">{error}</div>
    {/if}
  </div>

  {#if response}
    <!-- Оценка точности -->
    <div class="card">
      <h2>4. Оценка точности</h2>
      <div class="accuracy-grid">
        {#each activeMethods as key}
          {@const m = response.methods[key]}
          <div class="accuracy-card">
            <h3>{METHOD_LABELS[key]}</h3>
            {#if m.kind === 'one_step'}
              <div class="metric">Порядок точности: <b>p = {m.order}</b></div>
              <div class="metric">Оценка Рунге: <b>{m.runge_max.toExponential(3)}</b></div>
              <div class="metric">
                {m.runge_ok ? '✓ точность достигнута (R ≤ ε)' : '⚠ R > ε — уменьшите шаг'}
              </div>
            {:else if m.ys}
              <div class="metric">Порядок точности: <b>p = {m.order}</b></div>
              <div class="metric">max|yточн − yᵢ|: <b>{m.max_error.toExponential(3)}</b></div>
              <div class="metric">
                {m.max_error_ok ? '✓ точность достигнута' : '⚠ погрешность > ε'}
              </div>
            {:else}
              <div class="note">{m.note}</div>
            {/if}
          </div>
        {/each}
      </div>
    </div>

    <!-- Таблица -->
    <div class="card">
      <h2>5. Таблица значений</h2>
      <p class="info" style="margin-bottom:10px">Уравнение: <span class="eq-expr">{response.equation}</span></p>
      <div class="results-wrap">
        <table class="results-table">
          <thead>
            <tr>
              <th>i</th>
              <th>xᵢ</th>
              <th>Точное</th>
              {#each activeMethods as key}
                {#if response.methods[key].ys}
                  <th>{METHOD_LABELS[key]}</th>
                {/if}
              {/each}
            </tr>
          </thead>
          <tbody>
            {#each response.xs as x, i}
              <tr>
                <td class="idx">{i}</td>
                <td class="num">{x.toFixed(4)}</td>
                <td class="num">{response.exact[i].toFixed(6)}</td>
                {#each activeMethods as key}
                  {#if response.methods[key].ys}
                    <td class="num">{response.methods[key].ys[i].toFixed(6)}</td>
                  {/if}
                {/each}
              </tr>
            {/each}
          </tbody>
        </table>
      </div>
    </div>

    <!-- График -->
    <div class="card">
      <h2>6. График решения</h2>
      <div style="display:flex;justify-content:flex-end;margin-bottom:8px">
        <button class="btn btn-secondary btn-sm" on:click={() => chartInst && chartInst.resetZoom()}>
          ↺ Сбросить зум
        </button>
      </div>
      <div class="chart-wrap">
        <canvas bind:this={chartCanvas}></canvas>
      </div>
    </div>
  {/if}
</main>
