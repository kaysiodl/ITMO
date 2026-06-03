<script>
  export let onSubmit

  let points = Array.from({length: 8}, () => ({ x: '', y: '' }))
  let error = ''
  let activeTab = 'manual'
  let fileError = ''

  const EXAMPLE = [
    [1,2.1],[2,3.9],[3,6.2],[4,7.8],[5,10.1],[6,12.0],[7,13.9],[8,16.2]
  ]

  function addPoint() {
    if (points.length < 12) points = [...points, { x: '', y: '' }]
  }

  function removePoint(i) {
    if (points.length > 8) points = points.filter((_, idx) => idx !== i)
  }

  function validate(pts) {
    error = ''
    if (pts.length < 8 || pts.length > 12) {
      error = `Нужно от 8 до 12 точек. Сейчас: ${pts.length}`; return null
    }
    const x = [], y = []
    for (let i = 0; i < pts.length; i++) {
      const xi = parseFloat(pts[i].x), yi = parseFloat(pts[i].y)
      if (isNaN(xi) || isNaN(yi) || pts[i].x === '' || pts[i].y === '') {
        error = `Точка ${i+1}: некорректные значения`; return null
      }
      x.push(xi); y.push(yi)
    }
    if (new Set(x).size !== x.length) { error = 'Значения X должны быть уникальными'; return null }
    return { x, y }
  }

  function submit() {
    const res = validate(points)
    if (res) onSubmit(res.x, res.y)
  }

  function loadExample() {
    points = EXAMPLE.map(([x, y]) => ({ x: String(x), y: String(y) }))
    error = ''
  }

  function handleFile(e) {
    fileError = ''
    const file = e.target.files[0]
    if (!file) return
    const reader = new FileReader()
    reader.onload = (ev) => {
      const lines = ev.target.result.trim().split('\n').filter(l => l.trim())
      const parsed = []
      for (let i = 0; i < lines.length; i++) {
        const parts = lines[i].trim().split(/[\s,;]+/)
        if (parts.length < 2 || isNaN(parseFloat(parts[0])) || isNaN(parseFloat(parts[1]))) {
          fileError = `Строка ${i+1}: некорректный формат`; return
        }
        parsed.push({ x: parts[0], y: parts[1] })
      }
      if (parsed.length < 8 || parsed.length > 12) {
        fileError = `Файл должен содержать 8–12 точек (найдено ${parsed.length})`; return
      }
      points = parsed
      activeTab = 'manual'
      error = ''
    }
    reader.readAsText(file)
    e.target.value = ''
  }
</script>

<div class="input-block">
  <div class="input-header">
    <h2>Исходные данные</h2>
    <div class="tabs">
      <button class="tab" class:active={activeTab==='manual'} on:click={() => activeTab='manual'}>Вручную</button>
      <button class="tab" class:active={activeTab==='file'} on:click={() => activeTab='file'}>Из файла</button>
    </div>
  </div>

  {#if activeTab === 'manual'}
    <div class="points-grid">
      <div class="grid-header">
        <span>#</span><span>X</span><span>Y</span><span></span>
      </div>
      {#each points as pt, i}
        <div class="point-row">
          <span class="idx">{i+1}</span>
          <input type="number" step="any" bind:value={pt.x} placeholder="x" />
          <input type="number" step="any" bind:value={pt.y} placeholder="y" />
          <button class="remove" on:click={() => removePoint(i)} disabled={points.length <= 8} title="Удалить">×</button>
        </div>
      {/each}
    </div>

    <div class="row-actions">
      <button class="ghost" on:click={addPoint} disabled={points.length >= 12}>+ Добавить точку</button>
      <button class="ghost" on:click={loadExample}>Пример</button>
      <span class="count">{points.length}/12</span>
    </div>
  {:else}
    <div class="file-zone">
      <p class="hint">Формат файла: каждая строка — два числа (x и y), разделитель — пробел, запятая или точка с запятой. 8–12 строк.</p>
      <label class="file-label">
        <input type="file" accept=".txt,.csv" on:change={handleFile} />
        <span>Выбрать файл (.txt, .csv)</span>
      </label>
      {#if fileError}
        <div class="error">{fileError}</div>
      {/if}
      <p class="hint small">После загрузки файла точки появятся во вкладке «Вручную» для редактирования.</p>
    </div>
  {/if}

  {#if error}
    <div class="error">{error}</div>
  {/if}

  <button class="primary" on:click={submit}>Аппроксимировать</button>
</div>

<style>
  .input-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 1rem }
  .tabs { display: flex; gap: 0.25rem; background: #0d1117; padding: 3px; border-radius: 8px; border: 1px solid #21262d }
  .tab {
    padding: 0.3rem 0.85rem;
    border: none; border-radius: 6px;
    background: transparent; color: #8b949e;
    font-family: 'Onest', sans-serif; font-size: 0.82rem;
    cursor: pointer; transition: all 0.15s;
  }
  .tab.active { background: #21262d; color: #e6edf3 }

  .points-grid { display: flex; flex-direction: column; gap: 0.35rem }

  .grid-header {
    display: grid;
    grid-template-columns: 28px 1fr 1fr 28px;
    gap: 0.5rem;
    padding: 0 0.1rem;
    color: #8b949e; font-size: 0.75rem; font-weight: 500;
    text-align: center;
  }
  .grid-header span:nth-child(2), .grid-header span:nth-child(3) { text-align: left; padding-left: 0.5rem }

  .point-row {
    display: grid;
    grid-template-columns: 28px 1fr 1fr 28px;
    gap: 0.5rem;
    align-items: center;
  }

  .idx { color: #8b949e; font-size: 0.78rem; font-family: 'JetBrains Mono', monospace; text-align: center }

  .point-row input {
    width: 100%;
    background: #0d1117;
    border: 1px solid #30363d;
    border-radius: 6px;
    color: #c9d1d9;
    font-family: 'JetBrains Mono', monospace;
    font-size: 0.85rem;
    padding: 0.4rem 0.6rem;
    outline: none;
    transition: border-color 0.15s;
  }
  .point-row input:focus { border-color: #388bfd }
  .point-row input[type=number]::-webkit-inner-spin-button { -webkit-appearance: none }

  .remove {
    width: 28px; height: 28px;
    display: flex; align-items: center; justify-content: center;
    background: transparent; border: 1px solid #30363d;
    border-radius: 6px; color: #8b949e;
    font-size: 1rem; cursor: pointer; transition: all 0.15s;
    padding: 0;
  }
  .remove:hover:not(:disabled) { border-color: #f85149; color: #f85149 }
  .remove:disabled { opacity: 0.3; cursor: not-allowed }

  .row-actions { display: flex; align-items: center; gap: 0.5rem; margin-top: 0.75rem }
  .count { margin-left: auto; color: #8b949e; font-size: 0.78rem; font-family: 'JetBrains Mono', monospace }

  .file-zone { display: flex; flex-direction: column; gap: 0.75rem; padding: 1rem 0 }

  .file-label {
    display: inline-flex; align-items: center;
    cursor: pointer;
  }
  .file-label input { display: none }
  .file-label span {
    padding: 0.5rem 1rem;
    background: #0d1117; color: #c9d1d9;
    border: 1px dashed #30363d; border-radius: 6px;
    font-size: 0.85rem; transition: border-color 0.2s;
  }
  .file-label:hover span { border-color: #388bfd; color: #e6edf3 }

  .hint { color: #8b949e; font-size: 0.8rem }
  .hint.small { font-size: 0.75rem }
</style>
