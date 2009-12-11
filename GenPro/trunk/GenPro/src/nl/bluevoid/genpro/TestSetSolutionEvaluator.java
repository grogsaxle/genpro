package nl.bluevoid.genpro;

public abstract class TestSetSolutionEvaluator{

  protected final TestSet testSet;
  private static final int letterWrongpenalty = 1;
  protected ScoringType scoringType = ScoringType.SCORING_AVARAGE_PER_TESTCASE;

  public TestSetSolutionEvaluator() {
    this.testSet = createTestSet();
  }

  /**
   * default = SCORING_AVARAGE_PER_TESTCASE
   * 
   * @param scoringType
   */
  public void setScoringType(ScoringType scoringType) {
    this.scoringType = scoringType;
  }

  public ScoringType getScoringType() {
    return scoringType;
  }
  
  public abstract TestSet createTestSet();

  public abstract double evaluate(final Grid grid);

  public abstract TestSetStatistics getDeviations(final Grid grid);

  public abstract double scoreGrid(Grid g);

  public abstract double scoreGridException(Throwable t);

  public double getAbsoluteNumberDifference(final Number calculated, final Number expected) {
    return Math.abs(calculated.doubleValue() - expected.doubleValue());
  }

  public double getAbsoluteNumberDifference(final Number calculated, final NumberFeedback expected) {
    double calced = calculated.doubleValue();
    double expect = expected.value;
    if (expected.directive == NumberFeedback.HIGHER && calced < expect) {
      // System.out.println("HIGHER "+td.temperature+" calced "+temp);
      return getAbsoluteNumberDifference(calced, expect);
    } else if (expected.directive == NumberFeedback.LOWER && calced > expect) {
      // System.out.println("LOWER "+td.temperature+" calced "+temp);
      return getAbsoluteNumberDifference(calced, expect);
    } else
      // System.out.println(temp + " " + td);
      return 0;
  }

  public double getAbsoluteNumberDifferencePercentage(final Number calculated, final Number expected) {
    final double diff = Math.abs(calculated.doubleValue() - expected.doubleValue());
    return (diff / expected.doubleValue()) * 100;
  }

  protected double getStringDifference(String calculated, String expected) {
    // make sure there is a gradual score improvement
    int penalty = 0;
    if (calculated == null) {
      penalty = expected.length() * letterWrongpenalty;
    } else {
      // mogelijkheden:
      // 1 lettervergelijken op elke plek
      // 2
      penalty += Math.abs(calculated.length() - expected.length());
      for (int i = 0; i < expected.length() && i < calculated.length(); i++) {
        if (expected.charAt(i) != calculated.charAt(i)) {
          penalty += letterWrongpenalty;
        }
      }
    }
    return penalty;
  }

  public TestSet getTestSet() {
    return testSet;
  }
}
