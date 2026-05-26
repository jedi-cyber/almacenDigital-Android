function me(t){const o=pe(t),c=new Blob([o],{type:"text/html;charset=utf-8"}),i=URL.createObjectURL(c);window.open(i,"_blank"),setTimeout(()=>URL.revokeObjectURL(i),1e4)}function l(t){return Number(t.toFixed(2)).toString()}function d(t){return String(t??"").replace(/&/g,"&amp;").replace(/</g,"&lt;").replace(/>/g,"&gt;").replace(/"/g,"&quot;")}function se(t,o,c,i){const s=Math.round(t/(i.width/Math.max(1,Math.round(i.width/.5))))+1,a=Math.round(c/(i.depth/Math.max(1,Math.round(i.depth/.5))))+1,u=Math.round(o/(i.height/Math.max(1,i.sections??1)))+1;return`Col ${s} · Fila ${a} · Nivel ${u}`}function j(t){return t>70?"#ff6b6b":t>35?"#ffd45c":"#00e5a0"}function le(t,o){const u=t.length*38+20,v=t.map((p,f)=>{const y=o.filter(z=>z.shelfId===p.id),C=p.width*p.height*p.depth,R=y.reduce((z,S)=>z+S.item.width*S.item.height*S.item.depth,0),B=C>0?R/C*100:0,h=Math.max(2,384*B/100),x=f*38+10,$=j(B),O=p.label??p.id,A=O.length>8?O.slice(0,7)+"…":O;return`
      <text x="50" y="${x+28/2+5}" text-anchor="end" fill="#64748b" font-size="11" font-family="'Segoe UI',sans-serif">${A}</text>
      <rect x="56" y="${x}" width="${h}" height="28" rx="5" fill="${$}" opacity="0.85"/>
      <text x="${56+h+8}" y="${x+28/2+5}" fill="${$}" font-size="12" font-weight="700" font-family="'Segoe UI',sans-serif">${B.toFixed(0)}%</text>
    `}).join("");return`<svg viewBox="0 0 520 ${u}" xmlns="http://www.w3.org/2000/svg" style="width:100%;max-width:520px;display:block">${v}</svg>`}function ne(t,o){const c=o>0?t/o*100:0,i=60,s=90,a=90,u=2*Math.PI*i,v=c/100*u,p=j(c);return`
  <svg viewBox="0 0 180 180" xmlns="http://www.w3.org/2000/svg" style="width:180px;height:180px;flex-shrink:0">
    <circle cx="${s}" cy="${a}" r="${i}" fill="none" stroke="#1e2a3a" stroke-width="18"/>
    <circle cx="${s}" cy="${a}" r="${i}" fill="none" stroke="${p}" stroke-width="18"
      stroke-dasharray="${v.toFixed(2)} ${(u-v).toFixed(2)}"
      transform="rotate(-90 ${s} ${a})" stroke-linecap="round"/>
    <text x="${s}" y="${a-8}" text-anchor="middle" fill="${p}" font-size="22" font-weight="700" font-family="'Segoe UI',sans-serif">${c.toFixed(1)}%</text>
    <text x="${s}" y="${a+14}" text-anchor="middle" fill="#64748b" font-size="10" font-family="'Segoe UI',sans-serif">OCUPACIÓN</text>
    <circle cx="28" cy="148" r="6" fill="${p}"/>
    <text x="38" y="153" fill="#e2e8f0" font-size="10" font-family="'Segoe UI',sans-serif">Usado ${l(t)} m³</text>
    <circle cx="28" cy="165" r="6" fill="#1e2a3a"/>
    <text x="38" y="170" fill="#64748b" font-size="10" font-family="'Segoe UI',sans-serif">Libre ${l(o-t)} m³</text>
  </svg>`}function L(t,o){const c=new Map;return t.forEach(i=>{const s=o(i)?.trim()||"Sin definir",a=s.toLowerCase(),u=c.get(a)??{name:s,count:0};u.count+=1,c.set(a,u)}),[...c.values()].sort((i,s)=>s.count-i.count||i.name.localeCompare(s.name,"es"))}function V(t){const o=Math.max(1,Math.floor(t.sections??1)),i=[0,...t.boardOffsets&&t.boardOffsets.length>0?t.boardOffsets.map(s=>s*t.height):Array.from({length:o-1},(s,a)=>(a+1)*t.height/o),t.height].sort((s,a)=>s-a);return i.slice(0,-1).map((s,a)=>({section:a+1,label:t.sectionLabels?.[a]||`Piso ${a+1}`,min:s,max:i[a+1]}))}function H(t,o){const c=V(t),i=o.localPosition.y;return c.find((s,a)=>i>=s.min&&(i<s.max||a===c.length-1))?.section??1}function ce(t,o){const c=o.some(u=>u.id===t.shelfId),i=t.item.width>0&&t.item.height>0&&t.item.depth>0,s=Number.isFinite(t.localPosition.x)&&Number.isFinite(t.localPosition.y)&&Number.isFinite(t.localPosition.z),a=!!t.item.category?.trim()&&!!t.item.brand?.trim();return!c||!i||!s||!t.item.name?.trim()||!a}function de(t){const o=String(t??"");return/[",\n]/.test(o)?`"${o.replace(/"/g,'""')}"`:o}function pe(t){const{shelves:o,productsBySku:c,generatedAt:i,history:s=[]}=t,a=[...c.values()],u=new Set(s.filter(e=>e.action==="movido").map(e=>e.sku)),v=new Map;s.forEach(e=>{v.has(e.sku)||v.set(e.sku,e.createdAt)});const p=a.length,f=o.reduce((e,r)=>e+r.width*r.height*r.depth,0),y=a.reduce((e,r)=>e+r.item.width*r.item.height*r.item.depth,0),C=f-y,R=f>0?y/f*100:0,B=i.toLocaleString("es-PE",{day:"numeric",month:"long",year:"numeric",hour:"2-digit",minute:"2-digit"}),h=o.map(e=>{const r=a.filter(I=>I.shelfId===e.id),m=e.width*e.height*e.depth,b=r.reduce((I,k)=>I+k.item.width*k.item.height*k.item.depth,0),g=m>0?b/m*100:0;return{shelf:e,prods:r,vol:m,used:b,pct:g}}),x=h.reduce((e,r)=>r.pct>e.pct?r:e,h[0]),$=h.reduce((e,r)=>r.pct<e.pct?r:e,h[0]),A=[...a].sort((e,r)=>{const m=e.item.width*e.item.height*e.item.depth;return r.item.width*r.item.height*r.item.depth-m}).slice(0,3).map((e,r)=>{const m=e.item.width*e.item.height*e.item.depth;return`<div class="top-item"><span class="top-rank">${["🥇","🥈","🥉"][r]}</span><span class="top-name">${e.item.name||e.item.serialNumber||"Sin nombre"}</span><span class="top-vol">${l(m)} m³</span></div>`}).join(""),z=L(a,e=>e.item.category),S=L(a,e=>e.item.brand),N=a.filter(e=>ce(e,o)),D=a.filter(e=>!e.item.imageUrl),T=z.map(e=>`<option value="${d(e.name)}">${d(e.name)}</option>`).join(""),W=S.map(e=>`<option value="${d(e.name)}">${d(e.name)}</option>`).join(""),Z=o.map(e=>`<option value="${d(e.id)}">${d(e.id)} · ${d(e.label??e.id)}</option>`).join(""),q=[["Numero de serie","Nombre","Categoria","Marca","Estante","Piso","Ancho","Alto","Profundidad","Volumen","X","Y","Z"],...a.map(e=>{const r=o.find(g=>g.id===e.shelfId),m=r?H(r,e):"",b=e.item.width*e.item.height*e.item.depth;return[e.item.serialNumber??"",e.item.name,e.item.category??"Sin categoria",e.item.brand??"Sin marca",e.shelfId,m,l(e.item.width),l(e.item.height),l(e.item.depth),l(b),l(e.localPosition.x),l(e.localPosition.y),l(e.localPosition.z)]})].map(e=>e.map(de).join(",")).join(`
`),G=z.slice(0,5).map(e=>`<div class="intel-row"><span class="intel-label">${e.name}</span><span class="intel-value">${e.count} prod.</span></div>`).join(""),K=S.slice(0,5).map(e=>`<div class="intel-row"><span class="intel-label">${e.name}</span><span class="intel-value">${e.count} prod.</span></div>`).join(""),Y=N.slice(0,8).map(e=>`<div class="intel-row"><span class="intel-label">${e.item.serialNumber||"Sin serie"}</span><span class="intel-value warn">${e.item.name||"Sin nombre"} · ${e.shelfId||"Sin estante"}</span></div>`).join(""),_=ne(y,f),J=le(o,a),X=o.map(e=>`<button class="tab-btn" onclick="document.getElementById('shelf-${e.id}').scrollIntoView({behavior:'smooth'})">${e.id}</button>`).join(""),Q=o.map(e=>{const r=a.filter(n=>n.shelfId===e.id),m=e.width*e.height*e.depth,b=r.reduce((n,w)=>n+w.item.width*w.item.height*w.item.depth,0),g=m>0?b/m*100:0,I=Math.max(1,Math.floor(e.sections??1)),k=j(g),ee=g>70?'<span class="status-badge danger">⚠️ Lleno</span>':g>35?'<span class="status-badge warn">~ Moderado</span>':'<span class="status-badge ok">✔ Disponible</span>',te=g<10&&r.length>0?'<div class="shelf-alert">💡 Espacio libre &gt; 90% — considera reubicar productos aquí.</div>':"",ae=b>0?(r.length/m).toFixed(2):"0",re=V(e).map(n=>{const w=r.filter(E=>H(e,E)===n.section),F=e.width*e.depth*Math.max(0,n.max-n.min),U=w.reduce((E,M)=>E+M.item.width*M.item.height*M.item.depth,0),P=F>0?U/F*100:0;return`
        <div class="floor-row">
          <span>${n.label}</span>
          <div class="floor-bar"><i style="width:${Math.min(P,100).toFixed(1)}%;background:${j(P)}"></i></div>
          <strong>${w.length} prod. · ${P.toFixed(1)}%</strong>
        </div>`}).join(""),oe=r.length===0?'<tr><td colspan="6" class="empty-row">Sin productos registrados en este estante.</td></tr>':r.map(n=>{const w=n.item.width*n.item.height*n.item.depth,F=se(n.localPosition.x,n.localPosition.y,n.localPosition.z,e),U=n.item.category||"Sin categoria",P=n.item.brand||"Sin marca",E=u.has(n.item.sku),M=!!n.item.imageUrl,ie=v.get(n.item.sku)??"";return`
	          <tr data-report-product-row data-shelf="${d(n.shelfId)}" data-category="${d(U)}" data-brand="${d(P)}" data-moved="${E?"true":"false"}" data-has-image="${M?"true":"false"}" data-last-activity="${d(ie)}">
		            <td><span class="sku-badge">${d(n.item.serialNumber||"Sin serie")}</span></td>
	            <td>${d(n.item.name||"—")}</td>
	            <td class="catalog-cell">${d(U)}<br><small>${d(P)}</small></td>
	            <td class="dim-cell">${l(n.item.width)} × ${l(n.item.height)} × ${l(n.item.depth)}</td>
	            <td><span class="vol-chip">${l(w)} m³</span></td>
	            <td class="pos-cell">${d(F)}${E?'<br><small class="moved-chip">Movido</small>':""}${M?"":'<br><small class="missing-chip">Sin imagen</small>'}</td>
	          </tr>`}).join("");return`
	    <div class="shelf-card" id="shelf-${d(e.id)}" data-report-shelf-card data-shelf="${d(e.id)}" data-occupancy="${g.toFixed(2)}">
      <div class="shelf-card-head">
        <div class="shelf-id-badge">${e.id}</div>
        <div class="shelf-info">
          <h2 class="shelf-name">${e.label??e.id} ${ee}</h2>
          <p class="shelf-meta">${l(e.width)} × ${l(e.height)} × ${l(e.depth)} m &nbsp;·&nbsp; ${I} piso${I!==1?"s":""} &nbsp;·&nbsp; ${r.length} producto${r.length!==1?"s":""} &nbsp;·&nbsp; densidad: ${ae} prod/m³</p>
        </div>
        <div class="occupancy-ring">
          <svg viewBox="0 0 36 36">
            <circle class="ring-bg" cx="18" cy="18" r="15.9"/>
            <circle class="ring-fg" cx="18" cy="18" r="15.9" stroke="${k}" stroke-dasharray="${g.toFixed(1)} 100" transform="rotate(-90 18 18)"/>
          </svg>
          <span class="ring-label" style="color:${k}">${g.toFixed(0)}%</span>
          <span class="ring-sub">OCP.</span>
        </div>
      </div>
      <div class="progress-bar"><div class="progress-fill" style="width:${Math.min(g,100).toFixed(1)}%;background:${k}"></div></div>
      <p class="vol-label">${l(b)} / ${l(m)} m³ &nbsp;·&nbsp; libre: ${l(m-b)} m³</p>
      <div class="floor-occupancy">
        <strong>Ocupación por piso</strong>
        ${re}
      </div>
      ${te}
      <table class="product-table">
	        <thead><tr><th>SERIE</th><th>NOMBRE</th><th>CAT./MARCA</th><th>DIMENSIONES (M)</th><th>VOLUMEN</th><th>UBICACIÓN</th></tr></thead>
        <tbody>${oe}</tbody>
      </table>
    </div>`}).join("");return`<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="UTF-8"/>
<link rel="icon" type="image/svg+xml" href="data:image/svg+xml,<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24'><rect width='24' height='24' rx='4' fill='%230a0d14'/><path d='M3 13h4v8H3v-8Zm6-6h4v14H9V7Zm6 3h4v11h-4V10Z' fill='%2318c7ff'/></svg>"/>
<title>Reporte — Almacén Digital 3D</title>
<style>
*,*::before,*::after{box-sizing:border-box;margin:0;padding:0}
:root{--bg:#0a0d14;--surface:#111620;--surface2:#181f2e;--border:#1e2a3a;--text:#e2e8f0;--muted:#64748b;--accent:#18c7ff;--warn:#ffd45c;--danger:#ff6b6b;--success:#00e5a0}
body{background:var(--bg);color:var(--text);font-family:'Segoe UI',sans-serif;padding:2rem;max-width:1100px;margin:0 auto}
/* ─── Header ─── */
.report-brand{display:flex;align-items:center;gap:.75rem;margin-bottom:.5rem}
.brand-icon{width:2rem;height:2rem;flex-shrink:0}
.report-eyebrow{font-size:.7rem;letter-spacing:.18em;color:var(--accent);text-transform:uppercase}
.report-title{font-size:clamp(2rem,6vw,3.5rem);font-weight:700;letter-spacing:-.03em}
.report-title span{color:var(--accent)}
.report-meta{font-size:.8rem;color:var(--muted);margin-top:.5rem}
.report-meta b{color:var(--text)}
/* ─── KPIs ─── */
.kpi-grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(160px,1fr));gap:1rem;margin:1.5rem 0}
.kpi-card{background:var(--surface);border:1px solid var(--border);border-radius:12px;padding:1.2rem 1.4rem}
.kpi-label{font-size:.62rem;letter-spacing:.14em;text-transform:uppercase;color:var(--muted);margin-bottom:.4rem}
.kpi-value{font-size:1.9rem;font-weight:700;color:var(--accent)}
.kpi-value.warn{color:var(--warn)}
.kpi-value.ok{color:var(--success)}
.kpi-value.white{color:var(--text)}
.kpi-value.danger{color:var(--danger)}
.kpi-sub{font-size:.7rem;color:var(--muted);margin-top:.2rem}
/* ─── Sección de análisis ─── */
.analysis-grid{display:grid;grid-template-columns:1fr 1fr;gap:1rem;margin:1.5rem 0}
@media(max-width:700px){.analysis-grid{grid-template-columns:1fr}}
.analysis-card{background:var(--surface);border:1px solid var(--border);border-radius:12px;padding:1.2rem 1.4rem}
.analysis-title{font-size:.65rem;letter-spacing:.14em;text-transform:uppercase;color:var(--muted);margin-bottom:.9rem}
.intel-row{display:flex;justify-content:space-between;align-items:center;padding:.45rem 0;border-bottom:1px solid var(--border)}
.intel-row:last-child{border-bottom:none}
.intel-label{font-size:.8rem;color:var(--muted)}
.intel-value{font-size:.85rem;font-weight:700;color:var(--accent)}
.intel-value.danger{color:var(--danger)}
.intel-value.warn{color:var(--warn)}
.intel-value.ok{color:var(--success)}
.top-item{display:flex;align-items:center;gap:.6rem;padding:.4rem 0;border-bottom:1px solid var(--border)}
.top-item:last-child{border-bottom:none}
.top-rank{font-size:1rem}
.top-name{flex:1;font-size:.82rem;color:var(--text);white-space:nowrap;overflow:hidden;text-overflow:ellipsis}
.top-vol{font-size:.78rem;font-weight:700;color:var(--accent);white-space:nowrap}
/* ─── Gráficos ─── */
.charts-grid{display:grid;grid-template-columns:auto 1fr;gap:1rem;margin:1.5rem 0;align-items:center}
@media(max-width:600px){.charts-grid{grid-template-columns:1fr}}
.chart-card{background:var(--surface);border:1px solid var(--border);border-radius:12px;padding:1.2rem 1.4rem}
.chart-title{font-size:.65rem;letter-spacing:.14em;text-transform:uppercase;color:var(--muted);margin-bottom:1rem}
/* ─── Tabs ─── */
	.tabs{display:flex;flex-wrap:wrap;gap:.5rem;margin-bottom:1.8rem}
	.tab-btn{background:var(--surface);border:1px solid var(--border);border-radius:999px;color:var(--muted);font-size:.72rem;padding:.35rem .9rem;cursor:pointer}
	.tab-btn:hover{background:var(--surface2);color:var(--accent);border-color:var(--accent)}
	.report-filters{position:sticky;top:0;z-index:5;display:grid;grid-template-columns:repeat(4,minmax(0,1fr));gap:.75rem;margin:1.5rem 0;padding:1rem;background:rgba(17,22,32,.96);border:1px solid var(--border);border-radius:14px;backdrop-filter:blur(14px)}
	.report-filters label{display:grid;gap:.35rem;color:var(--muted);font-size:.68rem;font-weight:700;text-transform:uppercase;letter-spacing:.08em}
	.report-filters select,.report-filters input{min-height:36px;border:1px solid var(--border);border-radius:8px;background:var(--surface2);color:var(--text);padding:.4rem .55rem;font:inherit;font-size:.78rem}
	.filter-check{display:flex!important;align-items:center;gap:.5rem;text-transform:none;letter-spacing:0;font-size:.78rem}
	.filter-check input{min-height:auto}
	.filter-actions{display:flex;align-items:end;gap:.5rem}
	.filter-actions button{min-height:36px;border:1px solid var(--border);border-radius:8px;background:var(--surface2);color:var(--text);padding:0 .8rem;font-weight:700;cursor:pointer}
	.filter-summary{grid-column:1/-1;color:var(--muted);font-size:.78rem}
	@media(max-width:760px){body{padding:1rem}.report-filters{position:static;grid-template-columns:1fr}.product-table{display:block;overflow-x:auto}.shelf-card{padding:1rem}.shelf-card-head{display:grid}.occupancy-ring{width:52px;height:52px}}
	/* ─── Shelf cards ─── */
.shelf-card{background:var(--surface);border:1px solid var(--border);border-radius:16px;padding:1.5rem 1.8rem;margin-bottom:1.4rem;scroll-margin-top:1rem}
.shelf-card-head{display:flex;align-items:flex-start;gap:1rem;margin-bottom:1rem}
.shelf-id-badge{background:var(--surface2);border:1px solid var(--border);border-radius:8px;color:var(--accent);font-size:.7rem;font-weight:700;padding:.3rem .55rem;flex-shrink:0;margin-top:.3rem}
.shelf-info{flex:1}
.shelf-name{font-size:1.15rem;font-weight:700;display:flex;align-items:center;gap:.5rem;flex-wrap:wrap}
.shelf-meta{font-size:.75rem;color:var(--muted);margin-top:.25rem}
/* Status badge */
.status-badge{font-size:.65rem;font-weight:700;border-radius:999px;padding:.2rem .6rem;letter-spacing:.05em}
.status-badge.ok{background:rgba(0,229,160,.12);color:var(--success);border:1px solid rgba(0,229,160,.3)}
.status-badge.warn{background:rgba(255,212,92,.12);color:var(--warn);border:1px solid rgba(255,212,92,.3)}
.status-badge.danger{background:rgba(255,107,107,.12);color:var(--danger);border:1px solid rgba(255,107,107,.3)}
/* Occupancy ring */
.occupancy-ring{position:relative;width:60px;height:60px;flex-shrink:0;display:flex;align-items:center;justify-content:center}
.occupancy-ring svg{position:absolute;inset:0;width:100%;height:100%}
.ring-bg{fill:none;stroke:var(--border);stroke-width:3}
.ring-fg{fill:none;stroke-width:3;stroke-linecap:round}
.ring-label{position:absolute;font-size:.6rem;font-weight:700;top:38%;left:50%;transform:translate(-50%,-50%)}
.ring-sub{position:absolute;font-size:.38rem;color:var(--muted);top:62%;left:50%;transform:translate(-50%,-50%)}
.progress-bar{height:4px;background:var(--border);border-radius:999px;overflow:hidden;margin-bottom:.4rem}
.progress-fill{height:100%;border-radius:999px}
.vol-label{font-size:.72rem;color:var(--muted);text-align:right;margin-bottom:.75rem}
.floor-occupancy{background:var(--surface2);border:1px solid var(--border);border-radius:10px;padding:.7rem .8rem;margin-bottom:.85rem}
.floor-occupancy>strong{display:block;color:var(--text);font-size:.7rem;letter-spacing:.12em;text-transform:uppercase;margin-bottom:.5rem}
.floor-row{display:grid;grid-template-columns:90px 1fr 120px;gap:.6rem;align-items:center;padding:.25rem 0}
.floor-row span{color:var(--muted);font-size:.72rem}
.floor-row strong{color:var(--text);font-size:.72rem;text-align:right}
.floor-bar{height:7px;background:var(--border);border-radius:999px;overflow:hidden}
.floor-bar i{display:block;height:100%;border-radius:999px}
@media(max-width:620px){.floor-row{grid-template-columns:1fr}.floor-row strong{text-align:left}}
/* Alert */
.shelf-alert{background:rgba(255,212,92,.08);border:1px solid rgba(255,212,92,.25);border-radius:8px;color:var(--warn);font-size:.78rem;padding:.55rem .9rem;margin-bottom:.9rem}
/* Table */
.product-table{width:100%;border-collapse:collapse;font-size:.8rem}
.product-table th{text-align:left;font-size:.6rem;letter-spacing:.12em;color:var(--muted);padding:.5rem .7rem;border-bottom:1px solid var(--border)}
.product-table td{padding:.55rem .7rem;border-bottom:1px solid var(--border);color:var(--text);vertical-align:middle}
.product-table tr:last-child td{border-bottom:none}
.product-table tr:hover td{background:var(--surface2)}
.sku-badge{background:var(--surface2);border:1px solid var(--border);border-radius:6px;color:var(--accent);font-size:.7rem;padding:.15rem .45rem}
	.vol-chip{background:rgba(24,199,255,.1);color:var(--accent);border-radius:4px;font-size:.72rem;padding:.1rem .4rem}
	.moved-chip,.missing-chip{display:inline-block;margin-top:.2rem;border-radius:999px;padding:.1rem .4rem;font-size:.66rem;font-weight:800}
	.moved-chip{background:rgba(24,199,255,.12);color:var(--accent)}
	.missing-chip{background:rgba(255,212,92,.12);color:var(--warn)}
.dim-cell{color:var(--muted);font-size:.74rem}
.catalog-cell{color:var(--text);font-size:.74rem}
.catalog-cell small{color:var(--muted);font-size:.68rem}
.pos-cell{color:var(--muted);font-size:.72rem}
.empty-row{text-align:center;color:var(--muted);font-style:italic;padding:1.2rem!important}
/* ─── Sección print ─── */
.print-section{text-align:center;padding:2.5rem 0 1.5rem;border-top:1px solid var(--border);margin-top:2rem}
.print-section p{font-size:.78rem;color:var(--muted);margin-bottom:1rem}
.report-actions{display:flex;gap:.75rem;justify-content:center;flex-wrap:wrap}
.btn-print{background:var(--accent);color:#000;border:none;border-radius:10px;padding:.7rem 2rem;font-size:.9rem;font-weight:700;cursor:pointer;letter-spacing:.04em;transition:opacity .2s}
.btn-export{background:var(--surface);color:var(--text);border:1px solid var(--border);border-radius:10px;padding:.7rem 2rem;font-size:.9rem;font-weight:700;cursor:pointer;letter-spacing:.04em;transition:opacity .2s}
.btn-print:hover{opacity:.85}
.btn-export:hover{opacity:.85}
/* ─── Print media ─── */
@media print{
  body{background:#fff;color:#000;padding:1rem}
  :root{--bg:#fff;--surface:#f9f9f9;--surface2:#f0f0f0;--border:#ccc;--text:#111;--muted:#555;--accent:#0077aa;--warn:#b06000;--danger:#cc0000;--success:#006633}
  .tabs,.print-section,.btn-print,.btn-export{display:none!important}
  .shelf-card{break-inside:avoid;page-break-inside:avoid}
}
</style>
</head>
<body>

<!-- Header -->
<div class="report-brand">
  <svg class="brand-icon" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
    <path d="M3 13h4v8H3v-8Zm6-6h4v14H9V7Zm6 3h4v11h-4V10Z" fill="#18c7ff"/>
  </svg>
  <p class="report-eyebrow">Almacén Digital 3D · Reporte de Inventario</p>
</div>
<h1 class="report-title">Estado del <span>almacén</span></h1>
<p class="report-meta">Generado: <b>${B}</b> &nbsp;·&nbsp; <b>${o.length}</b> estantes &nbsp;·&nbsp; <b>${p}</b> productos</p>

<!-- KPIs -->
<div class="kpi-grid">
  <div class="kpi-card">
    <p class="kpi-label">Total Estantes</p>
    <p class="kpi-value white">${o.length}</p>
    <p class="kpi-sub">activos en escena</p>
  </div>
  <div class="kpi-card">
    <p class="kpi-label">Productos</p>
    <p class="kpi-value white">${p}</p>
    <p class="kpi-sub">registrados en BD</p>
  </div>
  <div class="kpi-card">
    <p class="kpi-label">Capacidad Total</p>
    <p class="kpi-value white">${l(f)}</p>
    <p class="kpi-sub">m³ capacidad bruta</p>
  </div>
  <div class="kpi-card">
    <p class="kpi-label">Volumen Usado</p>
    <p class="kpi-value warn">${l(y)}</p>
    <p class="kpi-sub">${R.toFixed(1)}% ocupación global</p>
  </div>
	  <div class="kpi-card">
	    <p class="kpi-label">Espacio Libre</p>
	    <p class="kpi-value ok">${l(C)}</p>
	    <p class="kpi-sub">m³ disponibles</p>
	  </div>
	  <div class="kpi-card">
	    <p class="kpi-label">Datos Incompletos</p>
	    <p class="kpi-value ${N.length>0?"danger":"ok"}">${N.length}</p>
	    <p class="kpi-sub">productos por revisar</p>
	  </div>
	</div>

<!-- Gráficos -->
<div class="charts-grid">
  <div class="chart-card">
    <p class="chart-title">Uso global (usado vs libre)</p>
    ${_}
  </div>
  <div class="chart-card">
    <p class="chart-title">Ocupación por estante</p>
    ${J}
  </div>
</div>

<!-- Indicadores inteligentes -->
<div class="analysis-grid">
  <div class="analysis-card">
    <p class="analysis-title">Ranking de estantes</p>
    ${x?`
    <div class="intel-row">
      <span class="intel-label">🔥 Más ocupado</span>
      <span class="intel-value danger">${x.shelf.label??x.shelf.id} (${x.pct.toFixed(0)}%)</span>
    </div>`:""}
    ${$?`
    <div class="intel-row">
      <span class="intel-label">🧊 Más vacío</span>
      <span class="intel-value ok">${$.shelf.label??$.shelf.id} (${$.pct.toFixed(0)}%)</span>
    </div>`:""}
    ${h.map(e=>`
    <div class="intel-row">
      <span class="intel-label">${e.shelf.id}</span>
      <span class="intel-value ${e.pct>70?"danger":e.pct>35?"warn":"ok"}">${e.pct.toFixed(1)}% · ${l(e.used)}/${l(e.vol)} m³</span>
    </div>`).join("")}
  </div>
	  <div class="analysis-card">
	    <p class="analysis-title">Top 3 productos por volumen</p>
	    ${A||'<p style="color:var(--muted);font-size:.8rem">Sin productos registrados.</p>'}
    <div style="margin-top:1rem">
      <p class="analysis-title" style="margin-bottom:.6rem">Eficiencia de almacenaje</p>
      <div class="intel-row">
        <span class="intel-label">Densidad global</span>
        <span class="intel-value">${f>0?(p/f).toFixed(2):"0"} prod/m³</span>
      </div>
      <div class="intel-row">
        <span class="intel-label">Volumen prom. por producto</span>
        <span class="intel-value">${p>0?l(y/p):"0"} m³</span>
      </div>
      <div class="intel-row">
        <span class="intel-label">Capacidad no usada</span>
        <span class="intel-value warn">${l(C)} m³</span>
	    </div>
	  </div>
	  <div class="analysis-card">
	    <p class="analysis-title">Categorias</p>
	    ${G||'<p style="color:var(--muted);font-size:.8rem">Sin categorias registradas.</p>'}
	  </div>
	  <div class="analysis-card">
	    <p class="analysis-title">Marcas</p>
	    ${K||'<p style="color:var(--muted);font-size:.8rem">Sin marcas registradas.</p>'}
	  </div>
	  <div class="analysis-card">
	    <p class="analysis-title">Productos con datos incompletos</p>
	    ${Y||'<p style="color:var(--muted);font-size:.8rem">No se detectaron productos incompletos.</p>'}
	  </div>
	</div>
</div>

	<!-- Detalle por estante -->
	<section class="report-filters" aria-label="Filtros del reporte">
	  <label>Categoria
	    <select id="filter-category"><option value="">Todas</option>${T}</select>
	  </label>
	  <label>Marca
	    <select id="filter-brand"><option value="">Todas</option>${W}</select>
	  </label>
	  <label>Estante
	    <select id="filter-shelf"><option value="">Todos</option>${Z}</select>
	  </label>
	  <label>Actividad desde
	    <input id="filter-date" type="date" />
	  </label>
	  <label>Capacidad usada
	    <select id="filter-capacity">
	      <option value="">Todos</option>
	      <option value="low">0% - 35%</option>
	      <option value="mid">36% - 70%</option>
	      <option value="high">Más de 70%</option>
	    </select>
	  </label>
	  <label class="filter-check"><input id="filter-moved" type="checkbox" /> Solo productos movidos</label>
	  <label class="filter-check"><input id="filter-no-image" type="checkbox" /> Solo productos sin imagen</label>
	  <div class="filter-actions">
	    <button type="button" id="clear-report-filters">Limpiar</button>
	  </div>
	  <p class="filter-summary" id="filter-summary">${p} productos visibles · ${D.length} sin imagen · ${u.size} movidos</p>
	</section>
	<div class="tabs">${X}</div>
${Q}

	<!-- Botón imprimir -->
	<div class="print-section">
	  <p>Exporta el detalle para auditoría o guarda una copia en PDF desde impresión.</p>
	  <div class="report-actions">
	    <button class="btn-print" onclick="window.print()">Imprimir / Guardar PDF</button>
	    <button class="btn-export" onclick="downloadProductsCsv()">Exportar Excel CSV</button>
	  </div>
	</div>
	
		<script>
		const productsCsv = ${JSON.stringify(q)};
		const filters = {
		  category: document.getElementById("filter-category"),
		  brand: document.getElementById("filter-brand"),
		  shelf: document.getElementById("filter-shelf"),
		  date: document.getElementById("filter-date"),
		  capacity: document.getElementById("filter-capacity"),
		  moved: document.getElementById("filter-moved"),
		  noImage: document.getElementById("filter-no-image"),
		  summary: document.getElementById("filter-summary")
		};
		function matchesCapacity(card) {
		  const value = filters.capacity.value;
		  const pct = Number(card.dataset.occupancy || 0);
		  if (!value) return true;
		  if (value === "low") return pct <= 35;
		  if (value === "mid") return pct > 35 && pct <= 70;
		  return pct > 70;
		}
		function applyReportFilters() {
		  let visibleProducts = 0;
		  document.querySelectorAll("[data-report-shelf-card]").forEach((card) => {
		    let visibleInShelf = 0;
		    const shelfOk = !filters.shelf.value || card.dataset.shelf === filters.shelf.value;
		    const capacityOk = matchesCapacity(card);
		    card.querySelectorAll("[data-report-product-row]").forEach((row) => {
		      const dateOk = !filters.date.value || (row.dataset.lastActivity && row.dataset.lastActivity.slice(0, 10) >= filters.date.value);
		      const ok = shelfOk
		        && capacityOk
		        && (!filters.category.value || row.dataset.category === filters.category.value)
		        && (!filters.brand.value || row.dataset.brand === filters.brand.value)
		        && (!filters.moved.checked || row.dataset.moved === "true")
		        && (!filters.noImage.checked || row.dataset.hasImage === "false")
		        && dateOk;
		      row.hidden = !ok;
		      if (ok) {
		        visibleProducts += 1;
		        visibleInShelf += 1;
		      }
		    });
		    card.hidden = !shelfOk || !capacityOk || visibleInShelf === 0;
		  });
		  filters.summary.textContent = visibleProducts === 0
		    ? "Sin resultados con los filtros actuales."
		    : visibleProducts + " producto" + (visibleProducts === 1 ? "" : "s") + " visible" + (visibleProducts === 1 ? "" : "s");
		}
		Object.values(filters).forEach((control) => {
		  if (control && control.id !== "filter-summary") control.addEventListener("input", applyReportFilters);
		});
		document.getElementById("clear-report-filters")?.addEventListener("click", () => {
		  filters.category.value = "";
		  filters.brand.value = "";
		  filters.shelf.value = "";
		  filters.date.value = "";
		  filters.capacity.value = "";
		  filters.moved.checked = false;
		  filters.noImage.checked = false;
		  applyReportFilters();
		});
		function downloadProductsCsv() {
	  const blob = new Blob([productsCsv], { type: "text/csv;charset=utf-8" });
	  const url = URL.createObjectURL(blob);
	  const link = document.createElement("a");
	  link.href = url;
	  link.download = "reporte-productos-almacen.csv";
	  document.body.appendChild(link);
	  link.click();
	  link.remove();
	  URL.revokeObjectURL(url);
	}
	<\/script>
	</body>
	</html>`}export{me as openReportWindow};
