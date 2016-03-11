package joeliven.tictactoe;

import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class AndroidTicTacToe extends ActionBarActivity {

//CLASS LEVEL FIELDS:
    // Represents the internal state of the game
    private TicTacToeGame mGame;

    // Class level variable to store the status of the game...whether or not it's over:
    private boolean mGameOver;

    // Whose turn to go first
    private char mTurn = TicTacToeGame.COMPUTER_PLAYER;

    // Buttons making up the board
    private Button mBoardButtons[];
    private Button mNewGameButton;

    // Various text displayed
    private TextView mInfoTextView;
    private TextView mHumanScoreTextView;
    private TextView mComputerScoreTextView;
    private TextView mTieScoreTextView;

    // Keep track of wins
    private int mHumanWins = 0;
    private int mComputerWins = 0;
    private int mTies = 0;

    // Board button IDs:
    private static final int[] BUTTON_IDS = {R.id.one, R.id.two, R.id.three, R.id.four,
            R.id.five, R.id.six, R.id.seven, R.id.eight, R.id.nine};



//OVERRIDE METHODS:
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mBoardButtons = new Button[TicTacToeGame.BOARD_SIZE];
        for (int i = 0; i < mBoardButtons.length; i++){
            mBoardButtons[i] = (Button) findViewById(BUTTON_IDS[i]);
        }//end for
        mNewGameButton = (Button) findViewById(R.id.newGame);
        mNewGameButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startNewGamePressed(v);
            }
        });
        mInfoTextView = (TextView) findViewById(R.id.information);
        mHumanScoreTextView = (TextView) findViewById(R.id.player_score);
        mComputerScoreTextView = (TextView) findViewById(R.id.computer_score);
        mTieScoreTextView = (TextView) findViewById(R.id.tie_score);

        mGame = new TicTacToeGame();

        startNewGame();
    }//end onCreate override method


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }//end onCreateOptionsMenu override method

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return true;
    }//end onOptionsItemSelected override method

//OTHER METHODS:
    public void startNewGamePressed(View v) {
        if(!mGameOver){
            mComputerWins++;
            mComputerScoreTextView.setText(Integer.toString(mComputerWins));
            mInfoTextView.setText(R.string.result_computer_wins);
        }
        startNewGame();
    }//end startNewGamePressed method

    private void startNewGame(){
        mGameOver = false;

        //clears the internal representation of the game:
        mGame.clearBoard();

        // Reset all buttons
        for (int i = 0; i < mBoardButtons.length; i++) {
            mBoardButtons[i].setText("");
            mBoardButtons[i].setEnabled(true);
            mBoardButtons[i].setOnClickListener(new ButtonClickListener(i));
        }//end for

        // Alternate who goes first
        if (mTurn == TicTacToeGame.HUMAN_PLAYER) {
            mTurn = TicTacToeGame.COMPUTER_PLAYER;
            mInfoTextView.setText(R.string.first_computer);
            int move = mGame.getComputerMove();
            setMove(TicTacToeGame.COMPUTER_PLAYER, move);
            mInfoTextView.setText(R.string.turn_human);
        }
        else {
            mTurn = TicTacToeGame.HUMAN_PLAYER;
            mInfoTextView.setText(R.string.first_human);
        }//end if else
    }//end startNewGame method

    private void setMove(char player, int location) {
        mGame.setMove(player, location);
        mBoardButtons[location].setEnabled(false);
        mBoardButtons[location].setText(String.valueOf(player));
        if (player == TicTacToeGame.HUMAN_PLAYER)
            mBoardButtons[location].setTextColor(Color.rgb(0, 200, 0));
        else
            mBoardButtons[location].setTextColor(Color.rgb(200, 0, 0));
    }//end setMove method

    // when game is over, disable all buttons and set flag
    private void gameOver() {
        mGameOver = true;
        for(int i = 0; i < mBoardButtons.length; i++)
            mBoardButtons[i].setEnabled(false);
    }//end gameOver method


//INNER CLASS:
        // Handles clicks on the game board buttons
        private class ButtonClickListener implements View.OnClickListener {
            int location;

            public ButtonClickListener(int location) {
                this.location = location;
            }

            public void onClick(View view) {
                if (!mGameOver && mBoardButtons[location].isEnabled()) {
                    setMove(TicTacToeGame.HUMAN_PLAYER, location);

                    // If no winner yet, let the computer make a move
                    int winner = mGame.checkForWinner();
                    if (winner == 0) {
                        mInfoTextView.setText(R.string.turn_computer);
                        int move = mGame.getComputerMove();
                        setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                        winner = mGame.checkForWinner();
                    }//end if

                    if (winner == 0){
                        mInfoTextView.setText(R.string.turn_human);
                    }else{
                        if (winner == 1){
                            mTies++;
                            mTieScoreTextView.setText(Integer.toString(mTies));
                            mInfoTextView.setText(R.string.result_tie);
                        }else if (winner == 2){
                            mHumanWins++;
                            mHumanScoreTextView.setText(Integer.toString(mHumanWins));
                            mInfoTextView.setText(R.string.result_human_wins);
                        }else{
                            mComputerWins++;
                            mComputerScoreTextView.setText(Integer.toString(mComputerWins));
                            mInfoTextView.setText(R.string.result_computer_wins);
                        }//end inner if else
                        gameOver();
                    }//end outer if else

                }//end outer if
            }//end onClick override method
        }//end ButtonClickListener inner Class.


}//end AndroidTicTacToe Class.
