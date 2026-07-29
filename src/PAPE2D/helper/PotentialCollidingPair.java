package PAPE2D.helper;

import PAPE2D.Body;
import PAPE2D.Fixture;

import java.util.ArrayList;
import java.util.List;

public class PotentialCollidingPair {
    // =================================================================================
    // Attributes
    // =================================================================================
    private Fixture fixture1;
    private Fixture fixture2;

    // =================================================================================
    // Bodies
    // =================================================================================

    /**
     * Get the first fixture of this potential colliding pair
     *
     * @return First fixture
     */
    public Fixture getFixture1() {
        return fixture1;
    }

    /**
     * Get the second fixture of this potential colliding pair
     *
     * @return Second fixture
     */
    public Fixture getFixture2() {
        return fixture2;
    }

    /**
     * Set the first fixture of this potential colliding pair
     *
     * @param fixture1 Given first fixture
     */
    private void setFixture1(Fixture fixture1) {
        this.fixture1 = fixture1;
    }

    /**
     * Set the second fixture of this potential colliding pair
     *
     * @param fixture2 Given second fixture
     */
    private void setFixture2(Fixture fixture2) {
        this.fixture2 = fixture2;
    }

    // =================================================================================
    // Constructor
    // =================================================================================

    /**
     * Create a new potential colliding pair with given fixtures
     *
     * @param fixture1 Given first fixture
     * @param fixture2 Given second fixture
     */
    public PotentialCollidingPair(Fixture fixture1, Fixture fixture2) {
        this.setFixture1(fixture1);
        this.setFixture2(fixture2);
    }

    // =================================================================================
    // Getting list of combined SAT axes
    // =================================================================================

    /**
     * Get the SAT axes associated with this potential colliding pair
     *
     * @return List containing every SAT axis for both fixtures
     */
    public List<Vector2> getSATAxes() {
        List<Vector2> output = new ArrayList<>(getFixture1().getSATAxes(getFixture2()));
        output.addAll(getFixture2().getSATAxes(getFixture1()));
        return output;
    }
}
