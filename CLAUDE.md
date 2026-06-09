# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Prj1_StockTrading** is a stock trading game/simulation GUI application built in Java with JavaFX. Players start with a balance and can buy/sell stocks in a simulated market that changes dynamically. The codebase demonstrates several design patterns and a layered architecture.

## Build & Run

This is an IntelliJ IDEA project with JUnit 5. There's no Maven or Gradle build system—the project uses classpath dependencies configured in the IDE.

**From IntelliJ:**
- Open the project and let IntelliJ index it
- Run the main class: `main.java` (JavaFX application entry point)
- Run a single unit test: right-click the test file → "Run"
- Run all unit tests: right-click the `test/unit` folder → "Run tests in..."

**From command line (if you have JDK installed):**
```bash
# Compile
javac -d out -sourcepath src:test src/main.java

# Run GUI (from project root)
java -cp out main

# Run tests (requires junit-jupiter on classpath)
java -cp out:test org.junit.platform.console.ConsoleLauncher --scan-classpath
```

## Architecture & Layers

### Domain Layer (`src/domain/`)
Core business entities: `Stock`, `Portfolio`, `OwnedStock`, `Transaction`, `StockPriceHistory`. These are pure data models with no dependencies on other layers.

### Business Layer (`src/business/`)

**Trading Services** (`business/services/trading/`)
- `BuySharesService` / `SellSharesService` — handle buy/sell transactions, check balances, calculate fees, update portfolio
- `GameService` — manages game lifecycle (reset, shutdown, historical data)
- `PortfolioQueryService` — queries for owned stocks and profit/loss
- `TransactionFeeCalculator` / Fee Strategies (`fees/`) — implements Strategy pattern for different fee models (flat, percentage, percentage-with-minimum)

**Market Services** (`business/services/market/`)
- `StockListenerService` — observer that records historical prices
- `StockAlertService` — observer that monitors for bankruptcy alerts
- `StockBankruptService` — handles stock bankruptcy logic

**Stock Market Simulation** (`business/stockmarket/`)
- `StockMarket` — singleton that manages live stocks and notifies observers (Observer pattern)
- `LiveStock` — wraps a domain Stock with simulation state
- `simulation/` — implements State pattern for stock price movements: `GrowingState`, `DecliningState`, `LiveStockState`, `BankruptState`, `SteadyState`
  - `TransitionManager` determines when to transition between states
  - `MarketTickerThread` updates all live stocks at regular intervals

**Other** (`business/observer/`, `business/commands/`, `business/dto/`)
- `StockMarketObserver` — interface for market event listeners
- Request/command objects for buy/sell operations
- DTOs for transferring data to presentation layer

### Persistence Layer (`src/persistence/`)

Uses **DAO (Data Access Object)** + **Unit of Work** pattern for file-based persistence.

- **Interfaces** (`interfaces/`)
  - `StockDAO`, `OwnedStockDAO`, `PortfolioDAO`, `TransactionDAO`, `StockPriceHistoryDAO`
  - `UnitOfWork` — transaction wrapper; coordinates changes across multiple DAOs
- **File Implementation** (`fileImplementation/`)
  - `StockFileDAO` etc. — read/write domain objects as JSON files
  - `FileUnitOfWork` — manages save/rollback, orchestrates all DAOs

Data is stored in `resources/data/` as JSON files (one per domain type).

### Presentation Layer (`src/presentation/`)

Uses **MVVM (Model-View-ViewModel)** with JavaFX.

- **Controllers** (`controllers/`) — handle UI events from JavaFX views
  - `MainMenuController`, `BuyStockController`, `SellStockController`, `PortfolioController`, etc.
- **ViewModels** (`viewModels/`) — expose state and methods for controllers to bind to
  - `MainMenuViewModel`, `BuyStockViewModel`, `SellStockViewModel`, `PortfolioViewModel`
- **Core** (`core/`)
  - `ViewManager` — loads and displays FXML views, manages scene transitions
  - `ApplicationContext` — dependency injection container; creates all services and injects them into controllers
  - `ControllerFactory` — creates controllers with their dependencies wired
  - `AcceptsStringArgument` — interface for screens that accept navigation arguments

JavaFX views are **not in the codebase** (likely stored externally or embedded in the IDE project). Views are loaded dynamically at runtime.

### Shared Layer (`src/shared/`)

- **Logging** (`logging/`)
  - Custom `Logger` singleton with `LoggerLevel` enum
  - `LogOutput` / `ConsoleLogOutput` — pluggable output destinations
  - `FileLogOutputter` — logs to file
- **Configuration** (`configuration/`)
  - `AppConfig` — singleton holding game settings (starting balance, transaction fee, bankruptcy penalty, data directory, etc.)
  - Uses Bill Pugh pattern for thread-safe singleton

## Design Patterns in Use

- **Observer** — `StockMarketObserver` interface; `StockMarket` notifies observers of price updates
- **State** — `StockState`/`LiveStockState` in market simulation; different stock price behaviors based on state
- **Strategy** — `FeeStrategy` interface; multiple fee calculation strategies
- **DAO/Unit of Work** — decouples domain logic from persistence; supports transactions across multiple entities
- **Factory** — `ControllerFactory` creates controllers with dependencies injected
- **Singleton** — `StockMarket`, `AppConfig`, `Logger`
- **MVVM** — controllers bind to view models; view models expose state for UI binding
- **Command** — `BuySharesRequest`, `SellSharesRequest` encapsulate trading requests

## Testing

Tests live in `test/unit/` and `test/integration/`.

- **Unit tests** use mock DAOs (`test/unit/mocks/`) to isolate services from persistence
- **Integration tests** may use real or quasi-real data
- All tests use **JUnit 5 (Jupiter)** with `@BeforeEach`, `@Test` annotations

**To run tests:**
- From IDE: right-click test folder or file → "Run tests in..."
- Single test: right-click test class → "Run"

**Key test files:**
- `BuySharesServiceTest`, `SellSharesServiceTest`, `GameServiceTest`, `PortfolioQueryServiceTest` — test core trading logic
- `BuySharesServiceIntTest`, `SellSharesServiceIntTest` — integration tests

## Key Entry Points

- **Application startup:** `main.java` → creates `ApplicationContext` → initializes `ViewManager` with the main menu
- **Dependency injection:** `ApplicationContext` wires all services and DAOs; `ControllerFactory` injects into controllers
- **Trading flow:** UI → `BuySharesController` → `BuySharesService` → multiple DAOs via `UnitOfWork` → persistence
- **Market updates:** `MarketTickerThread` calls `StockMarket.updateAllLiveStocks()` → notifies observers → UI updates

## Important Notes

- **Thread safety:** `StockMarket` and lists in market simulation use `CopyOnWriteArrayList` for thread-safe market updates in the background ticker thread
- **Singleton pattern:** `AppConfig`, `Logger`, and `StockMarket` are singletons; be aware when testing
- **File-based persistence:** No SQL database; data is JSON files. `UnitOfWork` pattern allows for atomic multi-file saves
- **JavaFX threading:** Controllers run on JavaFX UI thread; long operations should move to background tasks
- **Fee strategies:** Transaction fees are configurable via `FeeStrategy`; the fee calculation is injected into trading services

## Code Organization Tips

- Domain models (in `domain/`) should have no dependencies on business or persistence logic
- Services (in `business/services/`) orchestrate business logic; they depend on DAOs but not on presentation
- DAOs (in `persistence/`) implement interfaces from the same package; always program to interfaces
- Controllers should be thin; delegate to services and view models
- Use `Logger.getInstance()` for logging across the app
- Use `AppConfig.getInstance()` to access configuration values

## Exam Questions
For specific exam questions and tasks see the dedicated file:
- [Eksamensspørgsmål (ExQuestions.md)](./documents/presentations/ExQuestions.md)

