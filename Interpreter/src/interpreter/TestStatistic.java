package interpreter;

/**
 * class for keeping statistics of executed tests
 * @author st
 *
 */
public class TestStatistic {
	private String testName;	
	private int executionCount;
	private int failCount;
	
	public TestStatistic(String testName, int executionCount, int failCount) {
		this.testName=testName;
		this.executionCount=executionCount;
		this.failCount=failCount;
	}
	
	public String getTestName() {
    	return this.testName;
    }
    public int getExecutionCount() {
		return this.executionCount;
	}
    public int getFailCount() {
		return this.failCount;
	}
    public void setTestName(String testName) {
		this.testName = testName;
	}

	public void setExecutionCount(int executionCount) {
		this.executionCount = executionCount;
	}

	public void setFailCount(int failCount) {
		this.failCount = failCount;
	}
	
	/**
	 * helper method to compare all tests by execution count
	 * @param a
	 * @param b
	 * @return
	 */
	static int compareByExecutionCount(TestStatistic a, TestStatistic b)
    {
        return Integer.compare(a.getExecutionCount(), b.getExecutionCount());
    }
	
	/**
	 * helper method to compare all tests by fail count
	 * @param a
	 * @param b
	 * @return
	 */
    static int compareByFailCount(TestStatistic a, TestStatistic b)
    {
        return Integer.compare(a.getFailCount(), b.getFailCount());
    }
}
