import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

public class SimpleTest {
	
	@Value("${services.key}")
	private String injectedProp;

	public void hello() {
		System.out.println(injectedProp);
	}
	
}
