import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class test
{
  @Test
  void shouldAddTwoNumbers()
  {
    int a = 5;
    int b = 10;
    int expectedSum = 15;
    assertEquals(expectedSum, a + b);
  }

}
