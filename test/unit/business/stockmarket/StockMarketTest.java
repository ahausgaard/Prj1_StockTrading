package unit.business.stockmarket;

import business.dto.StockDTO;
import business.observer.StockMarketObserver;
import business.observer.StockUpdateEvent;
import business.stockmarket.StockMarket;
import domain.Stock;
import domain.StockState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StockMarketTest {

    private StockMarket market;

    @BeforeEach void setup() {
        market = StockMarket.getInstance();
        market.clearStocks(); // Ensure clean state
    }

    @Test void testStockMarketNotifiesObservers() {
        // Arrange: Create a mock observer
        MockObserver mockObserver = new MockObserver();
        market.addObserver(mockObserver);
        market.addNewStock("AAPL");

        // Act: Update all live stocks
        market.updateAllLiveStocks();

        // Assert: Verify that the observer was notified
        assertTrue(mockObserver.wasNotified(), "Observer should be notified");
        assertEquals(1, mockObserver.getCallCount(), "Observer should be called exactly once");
        assertNotNull(mockObserver.getLastEvent(), "Event should not be null");
        assertTrue(mockObserver.getLastEvent().stocks().size() > 0, "Event should contain stocks");
    }

    @Test void testMultipleObserversGetNotified() {
        // Arrange: Create multiple observers
        MockObserver observer1 = new MockObserver();
        MockObserver observer2 = new MockObserver();
        market.addObserver(observer1);
        market.addObserver(observer2);
        market.addNewStock("AAPL");

        // Act: Update stocks
        market.updateAllLiveStocks();

        // Assert: Both observers should be notified
        assertTrue(observer1.wasNotified(), "Observer1 should be notified");
        assertTrue(observer2.wasNotified(), "Observer2 should be notified");
    }

    @Test void testRemoveObserverStopsNotification() {
        // Arrange: Create and remove an observer
        MockObserver mockObserver = new MockObserver();
        market.addObserver(mockObserver);
        market.removeObserver(mockObserver);
        market.addNewStock("AAPL");

        // Act: Update stocks
        market.updateAllLiveStocks();

        // Assert: Removed observer should not be notified
        assertFalse(mockObserver.wasNotified(), "Removed observer should not be notified");
    }

    // ===== MOCK OBSERVER FOR TESTING =====
    // This mock observer implements StockMarketObserver to test that
    // StockMarket correctly notifies its observers.
    private static class MockObserver implements StockMarketObserver {
        private int callCount = 0;
        private StockUpdateEvent lastEvent = null;

        @Override
        public void update(StockUpdateEvent event) {
            callCount++;
            lastEvent = event;
        }

        boolean wasNotified() {
            return callCount > 0;
        }

        int getCallCount() {
            return callCount;
        }

        StockUpdateEvent getLastEvent() {
            return lastEvent;
        }
    }
}
