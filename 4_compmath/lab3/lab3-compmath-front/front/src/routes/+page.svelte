<script lang="ts">
	import { onMount } from 'svelte';
	import functionPlot from 'function-plot';
	import { fade, scale, fly } from 'svelte/transition';

	interface Equation { id: number; formula: string; view: string; }
	interface Method { id: number; label: string; }

	interface SolveResponse {
		value?: string;
		splits?: number;
		error: string | null;
	}

	const API_BASE = 'http://localhost:8080/lab3/api/integrals';

	let equations = $state<Equation[]>([]);
	let methods = $state<Method[]>([]);

	let selectedEquationId = $state<number>(0);
	let selectedMethodId = $state<number>(0);

	let a = $state<number>(0);
	let b = $state<number>(2);
	let eps = $state<number>(0.001);

	let result = $state<SolveResponse | null>(null);
	let isLoading = $state<boolean>(false);
	let errorMsg = $state<string | null>(null);

	let plotContainer: HTMLDivElement | undefined = $state();

	function extractRaw(jsonStr: string, key: string): string | undefined {
		const regex = new RegExp(`"${key}"\\s*:\\s*([^,}]+)`);
		const match = jsonStr.match(regex);
		if (!match) return undefined;
		return match[1].trim().replace(/^"|"$/g, '').substring(0, 15);
	}

	onMount(async () => {
		try {
			const [eqRes, methRes] = await Promise.all([
				fetch(`${API_BASE}/equations`),
				fetch(`${API_BASE}/methods`)
			]);
			equations = await eqRes.json();
			methods = await methRes.json();
		} catch (e) {
			errorMsg = "Сервер недоступен.";
		}
	});

	$effect(() => {
		if (plotContainer && equations.length > 0) drawPlot();
	});

	function drawPlot(): void {
		if (!plotContainer) return;
		try {
			const eq = equations.find(e => e.id === selectedEquationId);
			if (!eq) return;
			const rangeStart = Math.min(a, b);
			const rangeEnd = Math.max(a, b);

			functionPlot({
				target: plotContainer,
				width: 650,
				height: 450,
				grid: true,
				xAxis: { domain: [rangeStart - 1, rangeEnd + 1] },
				yAxis: { domain: [-10, 20] },
				data: [
					{ fn: eq.formula, color: '#db2777', nSamples: 1000 },
					{ fn: eq.formula, range: [rangeStart, rangeEnd], closed: true, color: 'rgba(219, 39, 119, 0.15)', skipTip: true }
				]
			});
		} catch (err) { console.error(err); }
	}

	async function handleSubmit() {
		isLoading = true; errorMsg = null; result = null;
		try {
			const res = await fetch(`${API_BASE}/solve`, {
				method: 'POST',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify({ equationId: selectedEquationId, methodId: selectedMethodId, a, b, eps })
			});
			const rawText = await res.text();
			const data = JSON.parse(rawText);
			if (data.error) errorMsg = data.error;
			else result = { splits: data.splits, error: data.error, value: extractRaw(rawText, 'value') };
		} catch { errorMsg = "Ошибка сети"; }
		finally { isLoading = false; }
	}
</script>

<div class="page-wrapper">
	<header class="top-nav">
		<div class="user-info">
			<h1>Матвеева Полина</h1>
			<p>Лабораторная работа №3 Численное интегрирование</p>
		</div>
	</header>

	<main class="main-grid">
		<div class="panel-group">
			<div class="panel graph-panel">
				<div class="plot-container" bind:this={plotContainer}></div>
			</div>

			{#if errorMsg}
				<div class="alert error" in:fade>{errorMsg}</div>
			{/if}

			{#if result}
				<div class="alert success" in:scale>
					<div class="res-row">
						<div class="res-item">
							<small>Результат</small>
							<strong>{result.value}</strong>
						</div>
						<div class="res-item">
							<small>Узлов (n)</small>
							<strong>{result.splits}</strong>
						</div>
					</div>
				</div>
			{/if}
		</div>

		<div class="panel controls">
			<section>
				<h3 class="label">Выберите функцию</h3>
				<div class="eq-list">
					{#each equations as eq}
						<button
							class="eq-card"
							class:active={selectedEquationId === eq.id}
							onclick={() => selectedEquationId = eq.id}
						>
							<div class="math-icon">
								<span class="limit top">{b}</span>
								<span class="integral-sign">∫</span>
								<span class="limit bot">{a}</span>
							</div>
							<span class="formula-text">{eq.view} dx</span>
						</button>
					{/each}
				</div>
			</section>

			<section class="config-section">
				<h3 class="label">Метод и параметры</h3>
				<select bind:value={selectedMethodId} class="glass-select">
					{#each methods as m}
						<option value={m.id}>{m.label}</option>
					{/each}
				</select>

				<div class="input-grid">
					<div class="glass-input">
						<label>Предел a</label>
						<input type="number" step="0.1" bind:value={a}>
					</div>
					<div class="glass-input">
						<label>Предел b</label>
						<input type="number" step="0.1" bind:value={b}>
					</div>
				</div>
				<div class="glass-input full">
					<label>Точность (ε)</label>
					<input type="number" step="0.000001" bind:value={eps}>
				</div>
			</section>

			<button class="submit-btn" onclick={handleSubmit} disabled={isLoading}>
				{isLoading ? 'Считаем...' : 'Вычислить'}
			</button>
		</div>
	</main>
</div>

<style>
	:global(body) { margin: 0; font-family: 'Inter', sans-serif; background: #fff; color: #1f2937; }

	.page-wrapper {
		min-height: 100vh;
		padding: 2rem;
		background: radial-gradient(circle at top left, #fdf2f8 0%, #fce7f3 100%);
	}

	.top-nav { max-width: 1200px; margin: 0 auto 2rem; }
	.top-nav h1 { margin: 0; font-size: 1.5rem; color: #be185d; font-weight: 800; }
	.top-nav p { margin: 0; color: #9d174d; opacity: 0.7; font-size: 0.9rem; }

	.main-grid {
		display: grid;
		grid-template-columns: 1fr 400px;
		gap: 2rem;
		max-width: 1200px;
		margin: 0 auto;
	}

	.panel {
		background: rgba(255, 255, 255, 0.7);
		backdrop-filter: blur(12px);
		border-radius: 24px;
		border: 1px solid rgba(255, 255, 255, 0.5);
		box-shadow: 0 10px 25px rgba(219, 39, 119, 0.05);
	}

	.controls { padding: 2rem; display: flex; flex-direction: column; gap: 1.5rem; }
	.panel-group { display: flex; flex-direction: column; gap: 1.5rem; }

	.label { font-size: 0.7rem; text-transform: uppercase; letter-spacing: 0.05em; color: #be185d; font-weight: 700; margin-bottom: 0.75rem; }

	.eq-list { display: flex; flex-direction: column; gap: 0.5rem; }
	.eq-card {
		display: flex; align-items: center; gap: 1rem;
		padding: 0.75rem 1rem; border-radius: 16px; border: 2px solid transparent;
		background: white; cursor: pointer; transition: 0.2s;
	}
	.eq-card.active { border-color: #ec4899; background: #fdf2f8; }

	.math-icon { display: flex; flex-direction: column; align-items: center; position: relative; min-width: 30px; font-family: serif; }
	.integral-sign { font-size: 1.8rem; line-height: 1; color: #ec4899; }
	.limit { font-size: 0.65rem; font-weight: 800; position: absolute; left: 18px; color: #374151; }
	.limit.top { top: -2px; }
	.limit.bot { bottom: -2px; }
	.formula-text { font-size: 0.95rem; font-weight: 500; }

	.glass-select, .glass-input input {
		width: 100%; padding: 0.75rem; border-radius: 12px;
		border: 1px solid #f9a8d4; background: white; outline: none; font-size: 0.9rem;
	}

	.input-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; margin: 1rem 0; width: 100%; }
	.glass-input { display: flex; flex-direction: column; gap: 0.3rem; min-width: 0; } /* min-width: 0 лечит вылеты UI */
	.glass-input label { font-size: 0.7rem; font-weight: 600; color: #9d174d; }

	.submit-btn {
		background: #db2777; color: white; border: none; padding: 1rem;
		border-radius: 16px; font-weight: 700; cursor: pointer; transition: 0.3s;
	}
	.submit-btn:hover { background: #be185d; transform: translateY(-2px); }

	.plot-container { padding: 1rem; display: flex; justify-content: center; }

	.alert { padding: 1.5rem; border-radius: 20px; font-weight: 600; }
	.alert.success { background: #fdf2f8; border: 1px solid #f9a8d4; color: #9d174d; }
	.res-row { display: flex; justify-content: space-around; }
	.res-item { display: flex; flex-direction: column; align-items: center; }
	.res-item strong { font-size: 1.4rem; font-family: monospace; }
</style>