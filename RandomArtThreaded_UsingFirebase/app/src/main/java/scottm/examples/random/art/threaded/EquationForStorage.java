package scottm.examples.random.art.threaded;

/** Stores data for an art equation. Designed to be stored
 * in backend.
 * Created by scottm on 6/22/2016.
 */
public class EquationForStorage {
    private String equation;
    private int id;
    private int upVotes;
    private int downVotes;
    private long timestamp;

    public EquationForStorage() {

    }

    public EquationForStorage(String equation, int id, int upVotes, int downVotes, long timeStamp) {
        this.equation = equation;
        this.id = id;
        this.upVotes = upVotes;
        this.downVotes = downVotes;
        this.timestamp = timeStamp;

    }


    public String getEquation() {
        return equation;
    }

    public void setEquation(String equation) {
        this.equation = equation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUpVotes() {
        return upVotes;
    }

    public void setUpVotes(int upVotes) {
        this.upVotes = upVotes;
    }

    public int getDownVotes() {
        return downVotes;
    }

    public void setDownVotes(int downVotes) {
        this.downVotes = downVotes;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
