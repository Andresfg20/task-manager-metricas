"""Lee los CSV de CK y lizard, genera resumen.md y falla si se superan los umbrales de RNF-03."""
import csv
import sys
from pathlib import Path


def umbrales():
    valores = {}
    for linea in Path("metricas/config.properties").read_text(encoding="utf-8").splitlines():
        if "=" in linea and not linea.startswith("#"):
            clave, valor = linea.split("=", 1)
            valores[clave.strip()] = valor.strip()
    return int(valores["umbral.vg.metodo"]), int(valores["umbral.wmc.clase"]), int(valores["umbral.cbo.clase"])


def leer(ruta):
    with open(ruta, encoding="utf-8") as archivo:
        return list(csv.DictReader(archivo))


def nombre_corto(clase):
    return clase.rsplit(".", 1)[-1]


def main(carpeta):
    vg_max, wmc_max, cbo_max = umbrales()
    salida = Path(carpeta)
    clases = leer(salida / "class.csv")
    metodos = leer(salida / "method.csv")

    clases.sort(key=lambda c: int(c["wmc"]), reverse=True)
    metodos.sort(key=lambda m: int(m["wmc"]), reverse=True)

    lineas = [f"# Resumen de métricas — {salida.name}", "",
              f"Clases medidas: {len(clases)} · Métodos medidos: {len(metodos)} · "
              f"LOC total: {sum(int(c['loc']) for c in clases)}", "",
              "## Clases (ordenadas por WMC)", "",
              "| Clase | WMC | CBO | RFC | LCOM | DIT | NOC | LOC | Estado |",
              "|---|---|---|---|---|---|---|---|---|"]
    fallos = []
    for c in clases:
        estado = "OK"
        if int(c["wmc"]) > wmc_max or int(c["cbo"]) > cbo_max:
            estado = "CRÍTICO"
            if int(c["wmc"]) > wmc_max:
                fallos.append(f"WMC {c['wmc']} > {wmc_max} en {c['class']}")
        lineas.append(f"| {nombre_corto(c['class'])} | {c['wmc']} | {c['cbo']} | {c['rfc']} | {c['lcom']} | "
                      f"{c['dit']} | {c['noc']} | {c['loc']} | {estado} |")

    lineas += ["", "## Métodos con mayor complejidad ciclomática V(G)", "",
               "| Clase | Método | V(G) | LOC |", "|---|---|---|---|"]
    for m in metodos[:15]:
        lineas.append(f"| {nombre_corto(m['class'])} | {m['method']} | {m['wmc']} | {m['loc']} |")
        if int(m["wmc"]) > vg_max:
            fallos.append(f"V(G) {m['wmc']} > {vg_max} en {m['class']}.{m['method']}")

    lineas += ["", f"Umbrales: V(G) ≤ {vg_max} por método, WMC ≤ {wmc_max} por clase "
               f"(CBO > {cbo_max} se marca como crítico, sin fallar).", "",
               "Resultado RNF-03: " + ("CUMPLE" if not fallos else "NO CUMPLE — " + "; ".join(fallos))]
    (salida / "resumen.md").write_text("\n".join(lineas) + "\n", encoding="utf-8")
    print("\n".join(lineas))
    return 1 if fallos else 0


if __name__ == "__main__":
    sys.exit(main(sys.argv[1]))
