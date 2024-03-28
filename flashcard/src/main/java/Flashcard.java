public class Flashcard {
    // Assuming external management of ID uniqueness
    private int id;
    private String question;
    private String answer;

    // Constructor used when creating a new flashcard without a known ID (for adding new flashcards)
    public Flashcard(String question, String answer) {
        // ID will be set separately if not using a counter
        this.question = question;
        this.answer = answer;
    }

    // Constructor used when loading flashcards from JSON, where ID is known
    public Flashcard(int id, String question, String answer) {
        this.id = id;
        this.question = question;
        this.answer = answer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
