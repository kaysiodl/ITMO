<script>
  export let result

  const TYPE_LABELS = {
    linear: 'Линейная',
    poly2: 'Полином 2°',
    poly3: 'Полином 3°',
    exponential: 'Экспоненциальная',
    logarithmic: 'Логарифмическая',
    power: 'Степенная',
  }

  function formatCoeffs(r) {
    const c = r.coeffs
    if (r.type === 'linear')      return `a=${fmt(c.a)}, b=${fmt(c.b)}`
    if (r.type === 'poly2')       return `a=${fmt(c.a)}, b=${fmt(c.b)}, c=${fmt(c.c)}`
    if (r.type === 'poly3')       return `a=${fmt(c.a)}, b=${fmt(c.b)}, c=${fmt(c.c)}, d=${fmt(c.d)}`
    if (r.type === 'exponential') return `a=${fmt(c.a)}, b=${fmt(c.b)}`
    if (r.type === 'logarithmic') return `a=${fmt(c.a)}, b=${fmt(c.b)}`
    if (r.type === 'power')       return `a=${fmt(c.a)}, b=${fmt(c.b)}`
    return ''
  }

  function fmt(v) { return typeof v === 'number' ? v.toFixed(4) : v }
</script>

<div class="results-section">
  <h2>Результаты аппроксимации</h2>
  <div class="table-wrap">
    <table>
      <thead>
        <tr>
          <th>Функция</th>
          <th>Коэффициенты</th>
          <th>S</th>
          <th>σ (СКО)</th>
          <th>R²</th>
          <th>Качество</th>
        </tr>
      </thead>
      <tbody>
        {#each result.results as r}
          <tr class:best={r.best}>
            <td>
              {TYPE_LABELS[r.type] || r.type}
              {#if r.best}<span class="badge">Лучшая</span>{/if}
            </td>
            <td class="mono small">{formatCoeffs(r)}</td>
            <td class="mono">{fmt(r.S)}</td>
            <td class="mono">{fmt(r.std_dev)}</td>
            <td class="mono">{fmt(r.R2)}</td>
            <td class="quality">{r.R2_message}</td>
          </tr>
          {#if r.type === 'linear' && r.pearson !== undefined}
          <tr class="sub-row">
            <td colspan="6">Коэффициент корреляции Пирсона: <span class="mono">{fmt(r.pearson)}</span></td>
          </tr>
          {/if}
        {/each}
      </tbody>
    </table>
  </div>

  <h3>Таблица значений (лучшая функция)</h3>
  {#each result.results.filter(r => r.best) as r}
    <div class="table-wrap">
      <table>
        <thead>
          <tr>
            <th>i</th>
            {#each result.x as xi, i}<th>{i+1}</th>{/each}
          </tr>
        </thead>
        <tbody>
          <tr><td>xᵢ</td>{#each result.x as v}<td class="mono">{fmt(v)}</td>{/each}</tr>
          <tr><td>yᵢ</td>{#each result.y as v}<td class="mono">{fmt(v)}</td>{/each}</tr>
          <tr><td>φ(xᵢ)</td>{#each r.phi as v}<td class="mono">{fmt(v)}</td>{/each}</tr>
          <tr><td>εᵢ</td>{#each r.eps as v}<td class="mono">{fmt(v)}</td>{/each}</tr>
        </tbody>
      </table>
    </div>
  {/each}
</div>
