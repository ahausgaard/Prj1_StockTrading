# Scenarie: Komplet handelssession (Happy Path)

## Kontekst

En spiller starter et nyt spil og gennemfører en fuld handelssession: køber aktier i to selskaber, markedet bevæger sig, og spilleren sælger med fortjeneste.

---

## Aktør

Spiller med en ny portefølje.

## Forudsætninger

- Spillet er startet med en startbalance på **10.000 DKK**
- To aktier er tilgængelige på markedet: **PNDORA** (100 DKK) og **NOVOB** (50 DKK)
- Transaktionsgebyr: **1%** (PercentageFeeStrategy)

---

## Trin

### Trin 1 — Køb PNDORA-aktier

Spilleren køber **5 PNDORA** à 100 DKK.

- Kostpris: 5 × 100 = 500 DKK
- Gebyr: 1% af 500 = 5 DKK
- **Ny balance: 9.495 DKK**

> Se: `src/business/services/trading/BuySharesService.java`

---

### Trin 2 — Køb NOVOB-aktier

Spilleren køber **10 NOVOB** à 50 DKK.

- Kostpris: 10 × 50 = 500 DKK
- Gebyr: 1% af 500 = 5 DKK
- **Ny balance: 8.990 DKK**
- Portefølje indeholder nu 2 aktietyper

> Se: `src/business/services/trading/BuySharesService.java`

---

### Trin 3 — Markedet opdateres

PNDORA-kursen stiger fra **100 → 120 DKK** (+20%).

> Se: `src/business/stockmarket/StockMarket.java`, `src/business/stockmarket/simulation/`

---

### Trin 4 — Sælg PNDORA med fortjeneste

Spilleren sælger **3 PNDORA** à 120 DKK.

- Provenu: 3 × 120 = 360 DKK
- Gebyr: 1% af 360 = 3,60 DKK
- **Netto indgang: 356,40 DKK**
- **Ny balance: 9.346,40 DKK**
- Resterende beholdning: 2 PNDORA, 10 NOVOB

> Se: `src/business/services/trading/SellSharesService.java`

---

### Trin 5 — Portfolioværdi beregnes

| Post | Beløb |
|------|-------|
| Likvid balance | 9.346,40 DKK |
| 2 PNDORA × 120 DKK | 240,00 DKK |
| 10 NOVOB × 50 DKK | 500,00 DKK |
| **Total porteføljeværdi** | **10.086,40 DKK** |

> Se: `src/business/services/trading/PortfolioQueryService.java`

---

### Trin 6 — Transaktionshistorik verificeres

| # | Type | Aktie | Antal | Kurs |
|---|------|-------|-------|------|
| 1 | KØB  | PNDORA | 5   | 100 DKK |
| 2 | KØB  | NOVOB  | 10  | 50 DKK  |
| 3 | SALG | PNDORA | 3   | 120 DKK |

> Se: `src/persistence/fileImplementation/TransactionFileDAO.java`

---

### Trin 7 — Profit/tab beregnes

Spilleren har realiseret en gevinst ved at sælge 3 PNDORA til en højere kurs end de blev købt.

- Netto profit/tab er **positiv** — vi solgte dyrere end vi købte
- Gebyrer er korrekt fratrukket i både køb og salg

> Se: `src/business/services/trading/PortfolioQueryService.java` → `getProfitLoss()`

---

## Forventet slutresultat

| Forventning | Resultat |
|-------------|----------|
| Balance reduceret ved hvert køb | ✓ |
| Balance øget ved salg | ✓ |
| OwnedStock-poster oprettet og opdateret | ✓ |
| 3 transaktioner gemt (2 køb, 1 salg) | ✓ |
| Porteføljeværdi = likvid + beholdning | ✓ |
| Nettoprofit positiv (solgt over kostpris) | ✓ |

---

## Lagene der samarbejder

```
Controller → BuySharesService / SellSharesService
               ↓
           UnitOfWork.begin()
               ↓
           PortfolioFileDAO · OwnedStockFileDAO · TransactionFileDAO · StockFileDAO
               ↓
           UnitOfWork.commit()  →  JSON-filer på disk
```

> Se: `src/persistence/fileImplementation/FileUnitOfWork.java`
