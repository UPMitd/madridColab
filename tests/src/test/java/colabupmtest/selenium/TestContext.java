package colabupmtest.selenium;

public class TestContext {

	private WebDriverManager webDriverManager;
private PageObjectManager pageObjectManager;
private ScenarioContext scenarioContext;

public TestContext () {
	 webDriverManager = new WebDriverManager();
	
	pageObjectManager = new PageObjectManager(webDriverManager.getDriver());
	
	scenarioContext = new ScenarioContext();


}
public WebDriverManager getDriverManager() {
	return webDriverManager;
}
public PageObjectManager getPageObjectManager(){
	return pageObjectManager;
}

public ScenarioContext getScenarioContext() {
return scenarioContext;
}
}
