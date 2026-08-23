package PAPE2D.helper;

import PAPE2D.Body;
import PAPE2D.Internal;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class that acts as a buffer holding contact records, by re-assigning records in a growing list
 */
public class ContactBuffer {
    // =================================================================================
    // Attributes
    // =================================================================================
    private List<ContactRecord> buffer = new ArrayList<>(256);
    private int activeCount = 0;

    // =================================================================================
    // Internal methods
    // =================================================================================

    /**
     * Reset the active count at the start of a solver frame
     */
    @Internal
    public void reset() {
        activeCount = 0;
    }

    /**
     * Add a collision contact to the buffer
     *
     * @param bodyA Given first body
     * @param bodyB Given second body
     * @param contactPoint Given contact point vector
     * @param contactNormal Given normal vector
     * @param contactTangent Given tangent vector
     * @param penetrationDepth Given penetration depth
     * @param contactImpulse Given contact impulse (after solving)
     */
    @Internal
    public void addContact(Body bodyA, Body bodyB, Vector2 contactPoint, Vector2 contactNormal, Vector2 contactTangent, double penetrationDepth, double contactImpulse) {
        if (activeCount == buffer.size()) {
            buffer.add(new ContactRecord());
        }

        ContactRecord record = buffer.get(activeCount);
        record.set(bodyA,bodyB,contactPoint,contactNormal,contactTangent,penetrationDepth,contactImpulse);
        activeCount++;
    }

    // =================================================================================
    // Public methods
    // =================================================================================

    /**
     * Get the number of contact records stored in the buffer at this time
     *
     * @return Number of stored contact records
     */
    public int getCount() {
        return activeCount;
    }

    /**
     * Get the contact record at a given index in the buffer
     *
     * @param index Given index
     *
     * @return Contact record stored at given index
     *
     * @throws IndexOutOfBoundsException If given index is out of current buffer bounds
     */
    public ContactRecord get(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= activeCount) throw new IndexOutOfBoundsException();

        return buffer.get(index);
    }
}
