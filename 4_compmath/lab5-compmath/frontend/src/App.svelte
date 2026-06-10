<script>
  import { tick } from 'svelte';
  import { Chart, registerables } from 'chart.js';
  import zoomPlugin from 'chartjs-plugin-zoom';
  import '../src/app.css';

  Chart.register(...registerables, zoomPlugin);

  let inputMode = 'manual';

  let nodes = [
    { x: '0.1', y: '1.25' },
    { x: '0.2', y: '2.38' },
    { x: '0.3', y: '3.79' },
    { x: '0.4', y: '5.44' },
    { x: '0.5', y: '7.14' },
  ];

  let uploadedFileName = '';
  let funcName = 'sin';
  let funcA = '0';
  let funcB = '3.14159';
  let funcN = '7';

  let xs = [];
  let ys = [];
  let xTarget = '0.35';

  let methods = {
    lagrange: true,
    newton_div_forward: true,
    newton_div_backward: true,
    gauss_forward: true,
    gauss_backward: true,
  };

  const METHOD_LABELS = {
    lagrange: 'Лагранж',
    newton_div_forward: 'Ньютон разд. (вперёд)',
    newton_div_backward: 'Ньютон разд. (назад)',
    gauss_forward: 'Гаусс 1-я ф-ла',
    gauss_backward: 'Гаусс 2-я ф-ла',
  };

  const EQUIDISTANT_ONLY = new Set(['gauss_forward', 'gauss_backward']);

  const CHART_COLORS = [
    '#58a6ff', '#3fb950', '#ff7b72', '#ffa657',
    '#d2a8ff', '#79c0ff', '#f0883e',
  ];

  let results = null;
  let equidistant = false;
  let diffTable = null;
  let divDiffTable = null;
  let chartXs = [];
  let chartData = {};
  let error = null;
  let loading = false;
  let showFinDiff = false;
  let showDivDiff = false;

  let chartCanvas;
  let chartInst = null;

  async function handleFileUpload(event) {
    const file = event.target.files[0];
    if (!file) return;
    uploadedFileName = file.name;
    const text = await file.text();
    const loadedXs = [], loadedYs = [];
    for (const line of text.split('\n')) {
      const clean = line.trim();
      if (!clean || clean.startsWith('#')) continue;
      const parts = clean.replace(/,/g, '.').split(/\s+/);
      if (parts.length >= 2) {
        const xv = parseFloat(parts[0]);
        const yv = parseFloat(parts[1]);
        if (!isNaN(xv) && !isNaN(yv)) {
          loadedXs.push(xv);
          loadedYs.push(yv);
        }
      }
    }
    if (loadedXs.length < 2) { error = 'Не удалось прочитать данные из файла'; return; }
    xs = loadedXs; ys = loadedYs;
    nodes = xs.map((x, i) => ({ x: String(x), y: String(ys[i]) }));
    error = null;
  }

  function addNode() {
    nodes = [...nodes, { x: '', y: '' }];
  }

  function removeNode(i) {
    if (nodes.length <= 2) return;
    nodes = nodes.filter((_, idx) => idx !== i);
  }

  function parseNodes() {
    const px = [], py = [];
    for (const n of nodes) {
      const xv = parseFloat(n.x.replace(',', '.'));
      const yv = parseFloat(n.y.replace(',', '.'));
      if (isNaN(xv) || isNaN(yv)) return 'Некорректные числа в таблице узлов';
      px.push(xv); py.push(yv);
    }
    if (px.length < 2) return 'Нужно минимум 2 точки';
    xs = px; ys = py;
    return null;
  }

  function generateFunc() {
    const a = parseFloat(funcA.replace(',', '.'));
    const b = parseFloat(funcB.replace(',', '.'));
    const n = parseInt(funcN);
    if (isNaN(a) || isNaN(b) || isNaN(n) || n < 2 || a >= b) {
      error = 'Некорректные параметры'; return;
    }
    xs = []; ys = [];
    for (let i = 0; i < n; i++) {
      const xi = a + (b - a) * i / (n - 1);
      let yi;
      if (funcName === 'sin') yi = Math.sin(xi);
      else if (funcName === 'cos') yi = Math.cos(xi);
      else if (funcName === 'exp') yi = Math.exp(xi);
      else if (funcName === 'sqrt') yi = Math.sqrt(xi);
      xs.push(xi); ys.push(yi);
    }
    nodes = xs.map((x, i) => ({ x: x.toFixed(5), y: ys[i].toFixed(6) }));
    error = null;
  }

  async function interpolate() {
    error = null;
    if (inputMode === 'manual') {
      const err = parseNodes();
      if (err) { error = err; return; }
    } else if (inputMode === 'file') {
      if (!xs.length) { error = 'Сначала загрузите файл'; return; }
    } else {
      if (!xs.length) { error = 'Сначала сгенерируйте точки'; return; }
    }

    const xt = parseFloat(xTarget.replace(',', '.'));
    if (isNaN(xt)) { error = 'Введите корректное значение x*'; return; }

    const selectedMethods = Object.entries(methods).filter(([, v]) => v).map(([k]) => k);
    if (!selectedMethods.length) { error = 'Выберите хотя бы один метод'; return; }

    loading = true;
    try {
      const res = await fetch('/api/interpolate', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ xs, ys, x_target: xt, methods: selectedMethods }),
      });
      const data = await res.json();
      if (data.error) { error = data.error; loading = false; return; }

      results = data.results;
      equidistant = data.equidistant;
      diffTable = data.diff_table;
      divDiffTable = data.div_diff_table;
      chartXs = data.chart_xs;
      chartData = data.chart_data;

      await tick();
      renderChart(xt, data);
    } catch {
      error = 'Ошибка соединения с сервером';
    }
    loading = false;
  }

  function renderChart(xt, data) {
    if (chartInst) { chartInst.destroy(); chartInst = null; }
    const datasets = [];

    datasets.push({
      label: 'Узлы',
      data: data.xs.map((x, i) => ({ x, y: data.ys[i] })),
      type: 'scatter',
      pointRadius: 7,
      backgroundColor: '#e6edf3',
      borderColor: '#0d1117',
      borderWidth: 2,
      order: 0,
    });

    let ci = 0;
    for (const [method, pts] of Object.entries(data.chart_data)) {
      datasets.push({
        label: METHOD_LABELS[method],
        data: data.chart_xs.map((x, i) => ({ x, y: pts[i] })),
        type: 'line',
        borderColor: CHART_COLORS[ci % CHART_COLORS.length],
        backgroundColor: 'transparent',
        pointRadius: 0,
        borderWidth: 2,
        tension: 0,
        order: 1,
      });
      ci++;
    }

    const firstResult = Object.values(data.results).find(v => v !== null);
    if (firstResult !== undefined) {
      datasets.push({
        label: `x* = ${xt}`,
        data: [{ x: xt, y: firstResult }],
        type: 'scatter',
        pointRadius: 9,
        pointStyle: 'triangle',
        backgroundColor: '#ff7b72',
        borderColor: '#0d1117',
        borderWidth: 2,
        order: 0,
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
          legend: {
            position: 'top',
            labels: { font: { size: 11 }, boxWidth: 14, color: '#8b949e' },
          },
          tooltip: {
            backgroundColor: '#161b22',
            borderColor: '#30363d',
            borderWidth: 1,
            titleColor: '#79c0ff',
            bodyColor: '#c9d1d9',
            callbacks: {
              label: ctx => `(${ctx.parsed.x.toFixed(5)},  ${ctx.parsed.y.toFixed(6)})`,
            },
          },
          zoom: {
            pan: { enabled: true, mode: 'xy' },
            zoom: {
              wheel: { enabled: true },
              pinch: { enabled: true },
              mode: 'xy',
            },
          },
        },
        scales: {
          x: {
            grid: { color: '#21262d' },
            ticks: { color: '#8b949e' },
            title: { display: true, text: 'x', color: '#79c0ff', font: { weight: 'bold' } },
          },
          y: {
            grid: { color: '#21262d' },
            ticks: { color: '#8b949e' },
            title: { display: true, text: 'y', color: '#79c0ff', font: { weight: 'bold' } },
          },
        },
      },
    });
  }

  function toggleMethod(key) {
    methods[key] = !methods[key];
    methods = { ...methods };
  }

  function selectAll() { Object.keys(methods).forEach(k => methods[k] = true); methods = { ...methods }; }
  function clearAll() { Object.keys(methods).forEach(k => methods[k] = false); methods = { ...methods }; }
</script>

<main>
  <h1>Интерполяция функции — ЛР №5</h1>

  <!-- Ввод данных -->
  <div class="card">
    <h2>1. Исходные данные</h2>
    <div class="tabs">
      <button class="tab" class:active={inputMode === 'manual'} on:click={() => inputMode = 'manual'}>Ручной ввод</button>
      <button class="tab" class:active={inputMode === 'file'} on:click={() => inputMode = 'file'}>Файл</button>
      <button class="tab" class:active={inputMode === 'func'} on:click={() => inputMode = 'func'}>Функция</button>
    </div>

    {#if inputMode === 'manual'}
      <div class="nodes-table-wrap">
        <table class="nodes-table">
          <thead>
            <tr>
              <th style="width:40px">№</th>
              <th>x</th>
              <th>y = f(x)</th>
              <th style="width:40px"></th>
            </tr>
          </thead>
          <tbody>
            {#each nodes as node, i}
              <tr>
                <td style="text-align:center;color:#6e7681;font-size:0.8rem">{i}</td>
                <td><input type="text" bind:value={node.x} placeholder="0.0" /></td>
                <td><input type="text" bind:value={node.y} placeholder="0.0" /></td>
                <td style="text-align:center">
                  <button class="del-btn" on:click={() => removeNode(i)} disabled={nodes.length <= 2} title="Удалить">✕</button>
                </td>
              </tr>
            {/each}
          </tbody>
        </table>
      </div>
      <button class="add-row-btn" on:click={addNode}>+ Добавить узел</button>

    {:else if inputMode === 'file'}
      <div class="form-group">
        <label>Прикрепить файл (.txt)</label>
        <input type="file" accept=".txt,.csv" on:change={handleFileUpload} style="color:#c9d1d9" />
        {#if uploadedFileName}
          <p class="info" style="margin-top:6px">Загружен: {uploadedFileName}</p>
        {:else}
        {/if}
      </div>
      {#if nodes.length > 0 && inputMode === 'file' && xs.length > 0}
        <div class="nodes-table-wrap" style="margin-top:12px">
          <table class="nodes-table">
            <thead><tr><th>№</th><th>x</th><th>y</th></tr></thead>
            <tbody>
              {#each nodes as node, i}
                <tr>
                  <td style="text-align:center;color:#6e7681;font-size:0.8rem">{i}</td>
                  <td><input type="text" bind:value={node.x} /></td>
                  <td><input type="text" bind:value={node.y} /></td>
                </tr>
              {/each}
            </tbody>
          </table>
        </div>
      {/if}

    {:else}
      <div class="row-3">
        <div class="form-group">
          <label>Функция</label>
          <select bind:value={funcName}>
            <option value="sin">sin(x)</option>
            <option value="cos">cos(x)</option>
            <option value="exp">e^x</option>
            <option value="sqrt">√x</option>
          </select>
        </div>
        <div class="form-group">
          <label>a</label>
          <input type="text" bind:value={funcA} />
        </div>
        <div class="form-group">
          <label>b</label>
          <input type="text" bind:value={funcB} />
        </div>
      </div>
      <div class="row" style="align-items:flex-end">
        <div class="form-group" style="margin-bottom:0">
          <label>Количество точек (n ≥ 2)</label>
          <input type="number" bind:value={funcN} min="2" max="20" />
        </div>
        <div>
          <button class="btn btn-secondary" on:click={generateFunc}>Сгенерировать</button>
        </div>
      </div>
      {#if xs.length > 0}
        <div class="nodes-table-wrap" style="margin-top:12px">
          <table class="nodes-table">
            <thead><tr><th>№</th><th>x</th><th>y</th></tr></thead>
            <tbody>
              {#each nodes as node, i}
                <tr>
                  <td style="text-align:center;color:#6e7681;font-size:0.8rem">{i}</td>
                  <td><input type="text" value={node.x} readonly /></td>
                  <td><input type="text" value={node.y} readonly /></td>
                </tr>
              {/each}
            </tbody>
          </table>
        </div>
      {/if}
    {/if}
  </div>

  <!-- Методы -->
  <div class="card">
    <h2>2. Методы интерполяции</h2>
    <div style="display:flex;gap:8px;align-items:center;margin-bottom:12px;flex-wrap:wrap">
      <button class="btn btn-secondary btn-sm" on:click={selectAll}>Все</button>
      <button class="btn btn-secondary btn-sm" on:click={clearAll}>Сбросить</button>
      {#if results !== null}
        <span class="badge" class:badge-green={equidistant} class:badge-yellow={!equidistant}>
          {equidistant ? '✓ Равноотстоящие' : '⚠ Неравноотстоящие'}
        </span>
      {/if}
    </div>
    <div class="methods-grid">
      {#each Object.keys(methods) as key}
        <label class="method-check" class:checked={methods[key]}>
          <input type="checkbox" checked={methods[key]} on:change={() => toggleMethod(key)} />
          {METHOD_LABELS[key]}
          {#if EQUIDISTANT_ONLY.has(key)}
            <span style="font-size:0.7rem;color:#6e7681;margin-left:auto">≡</span>
          {/if}
        </label>
      {/each}
    </div>
    <p class="info">Методы с «≡» — только для равноотстоящих узлов (Гаусс, Стирлинг, Бессель)</p>
  </div>

  <!-- x* -->
  <div class="card">
    <h2>3. Точка интерполяции x*</h2>
    <div class="x-target-row">
      <div class="form-group">
        <label>Значение x*</label>
        <input type="text" bind:value={xTarget} placeholder="0.35" />
      </div>
      <button class="btn btn-primary" on:click={interpolate} disabled={loading}>
        {loading ? 'Вычисление…' : '▶ Интерполировать'}
      </button>
    </div>
    {#if error}
      <div class="error">{error}</div>
    {/if}
  </div>

  <!-- Результаты -->
  {#if results}
    <div class="card">
      <h2>4. Результаты при x* = {xTarget}</h2>
      <table class="results-table">
        <thead>
          <tr><th>Метод</th><th>P(x*)</th></tr>
        </thead>
        <tbody>
          {#each Object.entries(results) as [key, val]}
            <tr>
              <td>{METHOD_LABELS[key]}</td>
              <td class="mono">
                {#if val !== null}
                  {val.toFixed(8)}
                {:else}
                  <span style="color:#6e7681">— (требует равноотстоящих узлов)</span>
                {/if}
              </td>
            </tr>
          {/each}
        </tbody>
      </table>
    </div>

    <!-- Таблицы разностей -->
    <div class="card">
      <h2>5. Таблицы разностей</h2>

      <button class="toggle-btn" on:click={() => showDivDiff = !showDivDiff}>
        {showDivDiff ? '▲' : '▼'} Разделённые разности (для Ньютона)
      </button>
      {#if showDivDiff && divDiffTable}
        <div class="diff-table-wrap" style="margin-bottom:12px">
          <table class="diff-table">
            <thead>
              <tr>
                <th>i</th>
                <th>f(xᵢ)</th>
                {#each { length: divDiffTable.length - 1 } as _, k}
                  <th>f[x₀…x{k+1}]</th>
                {/each}
              </tr>
            </thead>
            <tbody>
              {#each divDiffTable[0] as _, i}
                <tr>
                  <td style="color:#8b949e">{i}</td>
                  {#each divDiffTable as col}
                    {#if i < col.length}
                      <td class:highlight={i === 0}>{col[i].toFixed(6)}</td>
                    {:else}
                      <td></td>
                    {/if}
                  {/each}
                </tr>
              {/each}
            </tbody>
          </table>
        </div>
      {/if}

      {#if diffTable}
        <button class="toggle-btn" on:click={() => showFinDiff = !showFinDiff}>
          {showFinDiff ? '▲' : '▼'} Конечные разности (для Гаусса / Стирлинга / Бесселя)
        </button>
        {#if showFinDiff}
          <div class="diff-table-wrap">
            <table class="diff-table">
              <thead>
                <tr>
                  <th>i</th>
                  <th>yᵢ</th>
                  {#each { length: diffTable.length - 1 } as _, k}
                    <th>Δ{k+1}yᵢ</th>
                  {/each}
                </tr>
              </thead>
              <tbody>
                {#each diffTable[0] as _, i}
                  <tr>
                    <td style="color:#8b949e">{i}</td>
                    {#each diffTable as col}
                      {#if i < col.length}
                        <td class:highlight={i === 0}>{col[i].toFixed(6)}</td>
                      {:else}
                        <td></td>
                      {/if}
                    {/each}
                  </tr>
                {/each}
              </tbody>
            </table>
          </div>
        {/if}
      {:else}
        <p class="info" style="margin-top:6px">Конечные разности — только для равноотстоящих узлов</p>
      {/if}
    </div>

    <!-- График -->
    <div class="card">
      <h2>6. График</h2>
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
