<script>
  import PointsInput from './PointsInput.svelte'
  import ResultsTable from './ResultsTable.svelte'
  import ApproxChart from './ApproxChart.svelte'

  const API = 'http://localhost:8000'

  let result = null
  let loading = false
  let apiError = ''

  async function handleSubmit(x, y) {
    loading = true
    apiError = ''
    result = null
    try {
      const res = await fetch(`${API}/approximate`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ x, y })
      })
      const data = await res.json()
      if (!res.ok) {
        const detail = data.detail
        apiError = Array.isArray(detail)
          ? detail.map(d => d.msg).join('; ')
          : (detail || 'Ошибка сервера')
      } else {
        result = data
      }
    } catch (e) {
      apiError = 'Не удалось подключиться к серверу. Убедитесь, что бэкенд запущен на порту 8000.'
    } finally {
      loading = false
    }
  }
</script>

<main>
  <header>
    <div class="logo">Лабораторная №4</div>
    <h1>Матвеева Полина Павловна</h1>
  </header>

  <section class="content">
    <PointsInput onSubmit={handleSubmit} />

    {#if loading}
      <div class="loader">
        <div class="spinner"></div>
        <span>Вычисление...</span>
      </div>
    {/if}

    {#if apiError}
      <div class="api-error">{apiError}</div>
    {/if}

    {#if result}
      <ResultsTable {result} />
      <ApproxChart {result} />
    {/if}
  </section>
</main>

<style>
  :global(*) { box-sizing: border-box; margin: 0; padding: 0 }

  :global(body) {
    background: #0d1117;
    color: #c9d1d9;
    font-family: 'Onest', sans-serif;
    min-height: 100vh;
  }

  :global(h2) { font-size: 1.1rem; font-weight: 600; color: #e6edf3; margin-bottom: 1rem }
  :global(h3) { font-size: 0.95rem; font-weight: 600; color: #e6edf3; margin: 1.5rem 0 0.75rem }

  :global(.mono) { font-family: 'JetBrains Mono', monospace; font-size: 0.82rem }
  :global(.small) { font-size: 0.78rem }

  :global(textarea) {
    width: 100%;
    background: #161b22;
    border: 1px solid #30363d;
    border-radius: 6px;
    color: #c9d1d9;
    font-family: 'JetBrains Mono', monospace;
    font-size: 0.85rem;
    padding: 0.75rem;
    resize: vertical;
    outline: none;
    transition: border-color 0.2s;
  }
  :global(textarea:focus) { border-color: #388bfd }

  :global(button.primary) {
    margin-top: 1rem;
    padding: 0.6rem 1.4rem;
    background: #238636;
    color: #fff;
    border: none;
    border-radius: 6px;
    font-family: 'Onest', sans-serif;
    font-size: 0.9rem;
    cursor: pointer;
    transition: background 0.2s;
  }
  :global(button.primary:hover) { background: #2ea043 }

  :global(button.ghost) {
    padding: 0.35rem 0.85rem;
    background: transparent;
    color: #8b949e;
    border: 1px solid #30363d;
    border-radius: 6px;
    font-family: 'Onest', sans-serif;
    font-size: 0.82rem;
    cursor: pointer;
    transition: all 0.2s;
  }
  :global(button.ghost:hover) { color: #c9d1d9; border-color: #8b949e }

  :global(.error) {
    margin-top: 0.5rem;
    color: #f85149;
    font-size: 0.85rem;
  }

  :global(.hint) {
    color: #8b949e;
    font-size: 0.8rem;
    margin-bottom: 0.6rem;
  }

  :global(.input-block) {
    background: #161b22;
    border: 1px solid #30363d;
    border-radius: 10px;
    padding: 1.5rem;
  }

  :global(.input-header) {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 0.5rem;
  }

  :global(.table-wrap) {
    overflow-x: auto;
    border-radius: 8px;
    border: 1px solid #21262d;
  }

  :global(table) {
    width: 100%;
    border-collapse: collapse;
    font-size: 0.85rem;
  }

  :global(th) {
    background: #161b22;
    color: #8b949e;
    font-weight: 500;
    padding: 0.6rem 0.9rem;
    text-align: left;
    border-bottom: 1px solid #21262d;
    white-space: nowrap;
  }

  :global(td) {
    padding: 0.55rem 0.9rem;
    border-bottom: 1px solid #21262d;
    vertical-align: middle;
  }

  :global(tr:last-child td) { border-bottom: none }

  :global(tr.best) { background: rgba(35, 134, 54, 0.1) }

  :global(tr.sub-row td) {
    background: #0d1117;
    color: #8b949e;
    font-size: 0.8rem;
    padding: 0.4rem 0.9rem;
  }

  :global(.badge) {
    display: inline-block;
    margin-left: 0.4rem;
    padding: 0.1rem 0.45rem;
    background: #238636;
    color: #fff;
    border-radius: 10px;
    font-size: 0.7rem;
    font-weight: 600;
    vertical-align: middle;
  }

  :global(.quality) { font-size: 0.78rem; color: #8b949e }

  :global(.results-section), :global(.chart-section) {
    background: #161b22;
    border: 1px solid #30363d;
    border-radius: 10px;
    padding: 1.5rem;
  }

  :global(.chart-wrap) {
    height: 380px;
    position: relative;
  }

  header {
    text-align: center;
    padding: 3rem 1rem 2rem;
    border-bottom: 1px solid #21262d;
    margin-bottom: 2rem;
  }

  .logo {
    display: inline-block;
    width: 48px; height: 48px;
    background: #238636;
    color: #fff;
    font-family: 'JetBrains Mono', monospace;
    font-weight: 700;
    font-size: 0.85rem;
    border-radius: 12px;
    line-height: 48px;
    text-align: center;
    margin-bottom: 0.75rem;
  }

  h1 { font-size: 1.8rem; font-weight: 700; color: #e6edf3; margin-bottom: 0.3rem }
  p { color: #8b949e; font-size: 0.9rem }

  .content {
    max-width: 960px;
    margin: 0 auto;
    padding: 0 1rem 3rem;
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
  }

  .loader {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    color: #8b949e;
    font-size: 0.9rem;
  }

  .spinner {
    width: 20px; height: 20px;
    border: 2px solid #30363d;
    border-top-color: #238636;
    border-radius: 50%;
    animation: spin 0.8s linear infinite;
  }

  @keyframes spin { to { transform: rotate(360deg) } }

  .api-error {
    background: rgba(248, 81, 73, 0.1);
    border: 1px solid #f85149;
    color: #f85149;
    padding: 0.75rem 1rem;
    border-radius: 6px;
    font-size: 0.88rem;
  }
</style>
