<script>
  import { onMount, onDestroy } from 'svelte'
  import { Chart } from 'chart.js/auto'

  export let result

  let canvas
  let chart

  const COLORS = [
    '#e63946', '#2a9d8f', '#f4a261', '#457b9d',
    '#8338ec', '#fb8500', '#06d6a0'
  ]

  const TYPE_LABELS = {
    linear: 'Линейная', poly2: 'Полином 2°', poly3: 'Полином 3°',
    exponential: 'Экспоненциальная', logarithmic: 'Логарифмическая', power: 'Степенная'
  }

  function buildDatasets() {
    const xs = result.x
    const xMin = Math.min(...xs), xMax = Math.max(...xs)
    const step = (xMax - xMin) / 100
    const pts = Array.from({length: 101}, (_, i) => xMin + i * step)

    const datasets = [{
      label: 'Исходные данные',
      data: xs.map((x, i) => ({ x, y: result.y[i] })),
      type: 'scatter',
      pointRadius: 6,
      pointBackgroundColor: '#fff',
      pointBorderColor: '#fff',
      pointBorderWidth: 2,
      order: 0
    }]

    result.results.forEach((r, idx) => {
      const color = COLORS[idx % COLORS.length]
      datasets.push({
        label: TYPE_LABELS[r.type] + (r.best ? ' ★' : ''),
        data: xs.map((x, i) => ({ x, y: r.phi[i] })),
        type: 'line',
        borderColor: color,
        backgroundColor: 'transparent',
        borderWidth: r.best ? 3 : 1.5,
        borderDash: r.best ? [] : [4, 4],
        pointRadius: 0,
        tension: 0.4,
        order: r.best ? 1 : 2
      })
    })

    return datasets
  }

  onMount(() => {
    chart = new Chart(canvas, {
      type: 'line',
      data: { datasets: buildDatasets() },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        animation: { duration: 600 },
        plugins: {
          legend: {
            labels: { color: '#c9d1d9', font: { family: 'JetBrains Mono', size: 11 } }
          }
        },
        scales: {
          x: {
            type: 'linear',
            grid: { color: 'rgba(255,255,255,0.07)' },
            ticks: { color: '#8b949e', font: { family: 'JetBrains Mono' } }
          },
          y: {
            grid: { color: 'rgba(255,255,255,0.07)' },
            ticks: { color: '#8b949e', font: { family: 'JetBrains Mono' } }
          }
        }
      }
    })
  })

  onDestroy(() => chart?.destroy())
</script>

<div class="chart-section">
  <h2>Графики функций</h2>
  <div class="chart-wrap">
    <canvas bind:this={canvas}></canvas>
  </div>
</div>
