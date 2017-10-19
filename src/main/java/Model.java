
import java.util.*;

/**
 * Created by Caelestis on 12.10.2017.
 */
public class Model {
    private static final int FIELD_WIDTH = 4; // размер поля
    private Tile[][] gameTiles;               // игровое поле
    int score;                                // счет
    int maxTile;                              // значение макимальной плитки

    //для реализации возврата хода
    Stack<Integer> previousScores;
    Stack<Tile[][]> previousStates;
    private boolean isSaveNeeded = true;

    public Model() {
        resetGameTiles();
        this.score = 0;
        this.maxTile = 2;
        this.previousScores = new Stack<Integer>();
        this.previousStates = new Stack<Tile[][]>();
    }

    // вовзращает лист пустых клеток
    private List<Tile> getEmptyTiles() {
        List<Tile> result = new ArrayList<>();
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                if (gameTiles[i][j].value == 0) result.add(gameTiles[i][j]);
            }
        }
        return result;
    }

    // добавляет рандомно клетку 2 или 4 (соотношение 1 к 9)
    void addTile() {

        List<Tile> list = getEmptyTiles();
        if (list != null && list.size() != 0) {
            list.get((int) (list.size() * Math.random())).setValue(Math.random() < 0.9 ? 2 : 4);
        }
    }

    // сброс всех клеток
    void resetGameTiles() {
        this.gameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                this.gameTiles[i][j] = new Tile();
            }
        }
        addTile();
        addTile();
    }

    // сжатие одного ряда влево
    private boolean compressTiles(Tile[] tiles) {
        boolean isChanged = false;
        Tile temp;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (tiles[j].getValue() == 0 && tiles[j + 1].getValue() != 0) {
                    temp = tiles[j];
                    tiles[j] = tiles[j + 1];
                    tiles[j + 1] = temp;
                    isChanged = true;
                }
            }
        }
        return isChanged;
    }

    // сложение клеток
    private boolean mergeTiles(Tile[] tiles) {
        boolean isChanged = false;
        for (int j = 0; j < 3; j++) {
            if (tiles[j].getValue() != 0 && tiles[j].getValue() == tiles[j + 1].getValue()) {
                tiles[j].setValue(tiles[j].getValue() * 2);
                tiles[j + 1].setValue(0);
                if (tiles[j].getValue() > maxTile) maxTile = tiles[j].getValue();
                score += tiles[j].getValue();
                isChanged = true;

            }
        }

        if (isChanged) {
            Tile temp;
            for (int j = 0; j < 3; j++) {
                if (tiles[j].getValue() == 0 && tiles[j + 1].getValue() != 0) {
                    temp = tiles[j];
                    tiles[j] = tiles[j + 1];
                    tiles[j + 1] = temp;
                }
            }
        }

        return isChanged;
    }

    // методы для сдвига в четырех направлениях
    public void left() {
        if (isSaveNeeded) saveState(this.gameTiles);
        boolean isChanged = false;
        for (int i = 0; i < FIELD_WIDTH; i++) {
            if (compressTiles(gameTiles[i]) | mergeTiles(gameTiles[i])) {
                isChanged = true;
            }
        }
        if (isChanged) {
            addTile();
            isSaveNeeded = true;
        }


    }

    public void up() {
        saveState(this.gameTiles);
        rotate();
        left();
        rotate();
        rotate();
        rotate();
    }

    public void right() {
        saveState(this.gameTiles);
        rotate();
        rotate();
        left();
        rotate();
        rotate();
    }

    public void down() {
        saveState(this.gameTiles);
        rotate();
        rotate();
        rotate();
        left();
        rotate();
    }

    // поворот матрицы на 90 градусов против часовой стрелки
    private void rotate() {
        int len = FIELD_WIDTH;
        for (int k = 0; k < len / 2; k++) // border -> center
        {
            for (int j = k; j < len - 1 - k; j++) // left -> right
            {

                Tile tmp = gameTiles[k][j];
                gameTiles[k][j] = gameTiles[j][len - 1 - k];
                gameTiles[j][len - 1 - k] = gameTiles[len - 1 - k][len - 1 - j];
                gameTiles[len - 1 - k][len - 1 - j] = gameTiles[len - 1 - j][k];
                gameTiles[len - 1 - j][k] = tmp;
            }
        }
    }

    // геттер для поля
    public Tile[][] getGameTiles() {
        return gameTiles;
    }

    // проверка возможности хода
    public boolean canMove() {
        if (!getEmptyTiles().isEmpty())
            return true;
        for (int i = 0; i < gameTiles.length; i++) {
            for (int j = 1; j < gameTiles.length; j++) {
                if (gameTiles[i][j].value == gameTiles[i][j - 1].value)
                    return true;
            }
        }
        for (int j = 0; j < gameTiles.length; j++) {
            for (int i = 1; i < gameTiles.length; i++) {
                if (gameTiles[i][j].value == gameTiles[i - 1][j].value)
                    return true;
            }
        }
        return false;
    }

    // сохраняет состояние в стек
    private void saveState(Tile[][] field) {
        Tile[][] fieldToSave = new Tile[field.length][field[0].length];
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[0].length; j++) {
                fieldToSave[i][j] = new Tile(field[i][j].getValue());
            }
        }
        previousStates.push(fieldToSave);
        int scoreToSave = score;
        previousScores.push(scoreToSave);
        isSaveNeeded = false;
    }

    // откат на один ход назад
    public void rollback() {
        if (!previousStates.isEmpty() && !previousScores.isEmpty()) {
            this.score = previousScores.pop();
            this.gameTiles = previousStates.pop();
        }
    }

    // делает ход в случайном направлении
    public void randomMove() {
        switch (((int) (Math.random() * 100)) % 4) {
            case 0:
                left();
                break;
            case 1:
                up();
                break;
            case 2:
                right();
                break;
            case 3:
                down();
                break;
        }
    }

    // проверка измененя поля
    private boolean hasBoardChanged() {
        boolean result = false;
        int sumNow = 0;
        int sumPrevious = 0;
        Tile[][] tmp = previousStates.peek();
        for (int i = 0; i < gameTiles.length; i++) {
            for (int j = 0; j < gameTiles[0].length; j++) {
                sumNow += gameTiles[i][j].getValue();
                sumPrevious += tmp[i][j].getValue();
            }
        }
        return sumNow != sumPrevious;
    }

    // проверка эффективности хода
    private MoveEfficiency getMoveEfficiency(Move move) {
        MoveEfficiency moveEfficiency;
        move.move();
        if (hasBoardChanged()) moveEfficiency = new MoveEfficiency(getEmptyTiles().size(), score, move);
        else moveEfficiency = new MoveEfficiency(-1, 0, move);
        rollback();

        return moveEfficiency;
    }

    // реализация выбора эффективного хода из возможных
    public void autoMove() {
        PriorityQueue<MoveEfficiency> queue = new PriorityQueue(4, Collections.reverseOrder());
        queue.add(getMoveEfficiency(this::left));
        queue.add(getMoveEfficiency(this::right));
        queue.add(getMoveEfficiency(this::up));
        queue.add(getMoveEfficiency(this::down));
        Move move = queue.peek().getMove();
        move.move();
    }

    public void hyperMove() {
        ArrayList<MoveEfficiency> superEf = new ArrayList<>();
        ArrayList<MoveEfficiency> subLeft = new ArrayList<>();
        ArrayList<MoveEfficiency> subRight = new ArrayList<>();
        ArrayList<MoveEfficiency> subUp = new ArrayList<>();
        ArrayList<MoveEfficiency> subDown = new ArrayList<>();

        left();
        if (hasBoardChanged()) subLeft = intelMove();
        else subLeft.add(new MoveEfficiency(-1, 0));
        rollback();

        right();
        if (hasBoardChanged()) subRight = intelMove();
        else subRight.add(new MoveEfficiency(-1, 0));
        rollback();

        up();
        if (hasBoardChanged()) subUp = intelMove();
        else subUp.add(new MoveEfficiency(-1, 0));
        rollback();

        down();
        if (hasBoardChanged()) subDown = intelMove();
        else subDown.add(new MoveEfficiency(-1, 0));
        rollback();

        MoveEfficiency left = subLeft.get(0);
        MoveEfficiency right = subRight.get(0);
        MoveEfficiency up = subUp.get(0);
        MoveEfficiency down = subDown.get(0);

        superEf.add(left);
        superEf.add(right);
        superEf.add(up);
        superEf.add(down);

        Collections.sort(superEf);
        Collections.reverse(superEf);

        if (superEf.get(0).equals(left)){left();}
        else if (superEf.get(0).equals(right)){right();}
        else if (superEf.get(0).equals(up)){up();}
        else if (superEf.get(0).equals(down)){down();}
    }

    public void superMove() {
        ArrayList<MoveEfficiency> superEf = new ArrayList<>();
        ArrayList<MoveEfficiency> subLeft = new ArrayList<>();
        ArrayList<MoveEfficiency> subRight = new ArrayList<>();
        ArrayList<MoveEfficiency> subUp = new ArrayList<>();
        ArrayList<MoveEfficiency> subDown = new ArrayList<>();

        left();
        if (hasBoardChanged()) subLeft = subMove();
        else subLeft.add(new MoveEfficiency(-1, 0));
        rollback();

        right();
        if (hasBoardChanged()) subRight = subMove();
        else subRight.add(new MoveEfficiency(-1, 0));
        rollback();

        up();
        if (hasBoardChanged()) subUp = subMove();
        else subUp.add(new MoveEfficiency(-1, 0));
        rollback();

        down();
        if (hasBoardChanged()) subDown = subMove();
        else subDown.add(new MoveEfficiency(-1, 0));
        rollback();

        MoveEfficiency left = subLeft.get(0);
        MoveEfficiency right = subRight.get(0);
        MoveEfficiency up = subUp.get(0);
        MoveEfficiency down = subDown.get(0);

        superEf.add(left);
        superEf.add(right);
        superEf.add(up);
        superEf.add(down);

        Collections.sort(superEf);
        Collections.reverse(superEf);

        if (superEf.get(0).equals(left)){left();}
        else if (superEf.get(0).equals(right)){right();}
        else if (superEf.get(0).equals(up)){up();}
        else if (superEf.get(0).equals(down)){down();}
    }


    // интеллектуальная реализация выбора эффективного хода из возможных
    public void intelligentMove() {
        ArrayList<MoveEfficiency> ef = new ArrayList<>();

        MoveEfficiency left = new MoveEfficiency(-1, 0);
        MoveEfficiency leftLeft = new MoveEfficiency(-1, 0);
        MoveEfficiency leftRight = new MoveEfficiency(-1, 0);
        MoveEfficiency leftUp = new MoveEfficiency(-1, 0);
        MoveEfficiency leftDown = new MoveEfficiency(-1, 0);

        MoveEfficiency right = new MoveEfficiency(-1, 0);
        MoveEfficiency rightLeft = new MoveEfficiency(-1, 0);
        MoveEfficiency rightRight = new MoveEfficiency(-1, 0);
        MoveEfficiency rightUp = new MoveEfficiency(-1, 0);
        MoveEfficiency rightDown = new MoveEfficiency(-1, 0);

        MoveEfficiency up = new MoveEfficiency(-1, 0);
        MoveEfficiency upLeft = new MoveEfficiency(-1, 0);
        MoveEfficiency upRight = new MoveEfficiency(-1, 0);
        MoveEfficiency upUp = new MoveEfficiency(-1, 0);
        MoveEfficiency upDown = new MoveEfficiency(-1, 0);

        MoveEfficiency down = new MoveEfficiency(-1, 0);
        MoveEfficiency downLeft = new MoveEfficiency(-1, 0);
        MoveEfficiency downRight = new MoveEfficiency(-1, 0);
        MoveEfficiency downUp = new MoveEfficiency(-1, 0);
        MoveEfficiency downDown = new MoveEfficiency(-1, 0);

        left();
        if (hasBoardChanged()) {
            left = new MoveEfficiency(getEmptyTiles().size(), score);
            left();
            if (hasBoardChanged()) {
                leftLeft = new MoveEfficiency(getEmptyTiles().size(), score);
            } else leftLeft = left;
            ef.add(leftLeft);
            rollback();

            right();
            if (hasBoardChanged()) {
                leftRight = new MoveEfficiency(getEmptyTiles().size(), score);
            } else leftRight = left;
            ef.add(leftRight);
            rollback();

            up();
            if (hasBoardChanged()) {
                leftUp = new MoveEfficiency(getEmptyTiles().size(), score);
            } else leftUp = left;
            ef.add(leftUp);
            rollback();

            down();
            if (hasBoardChanged()) {
                leftDown = new MoveEfficiency(getEmptyTiles().size(), score);
            } else leftDown = left;
            ef.add(leftDown);
            rollback();
        }
        rollback();


        right();
        if (hasBoardChanged()) {
            right = new MoveEfficiency(getEmptyTiles().size(), score);
            left();
            if (hasBoardChanged()) {
                rightLeft = new MoveEfficiency(getEmptyTiles().size(), score);
            } else rightLeft = right;
            ef.add(rightLeft);
            rollback();

            right();
            if (hasBoardChanged()) {
                rightRight = new MoveEfficiency(getEmptyTiles().size(), score);
            } else rightRight = right;
            ef.add(rightRight);
            rollback();

            up();
            if (hasBoardChanged()) {
                rightUp = new MoveEfficiency(getEmptyTiles().size(), score);
            } else rightUp = right;
            ef.add(rightUp);
            rollback();

            down();
            if (hasBoardChanged()) {
                rightDown = new MoveEfficiency(getEmptyTiles().size(), score);
            } else rightDown = right;
            ef.add(rightDown);
            rollback();
        }
        rollback();


        up();
        if (hasBoardChanged()) {
            up = new MoveEfficiency(getEmptyTiles().size(), score);
            left();
            if (hasBoardChanged()) {
                upLeft = new MoveEfficiency(getEmptyTiles().size(), score);
            } else upLeft = up;
            ef.add(upLeft);
            rollback();


            right();
            if (hasBoardChanged()) {
                upRight = new MoveEfficiency(getEmptyTiles().size(), score);
            } else upRight = up;
            ef.add(upRight);
            rollback();


            up();
            if (hasBoardChanged()) {
                upUp = new MoveEfficiency(getEmptyTiles().size(), score);
            } else upUp = up;
            ef.add(upUp);
            rollback();


            down();
            if (hasBoardChanged()) {
                upDown = new MoveEfficiency(getEmptyTiles().size(), score);
            } else upDown = up;
            ef.add(upDown);
            rollback();
        }
        rollback();


        down();
        if (hasBoardChanged()) {
            down = new MoveEfficiency(getEmptyTiles().size(), score);
            left();
            if (hasBoardChanged()) {
                downLeft = new MoveEfficiency(getEmptyTiles().size(), score);
            } else downLeft = down;
            ef.add(downLeft);
            rollback();


            right();
            if (hasBoardChanged()) {
                downRight = new MoveEfficiency(getEmptyTiles().size(), score);
            } else downRight = down;
            ef.add(downRight);
            rollback();


            up();
            if (hasBoardChanged()) {
                downUp = new MoveEfficiency(getEmptyTiles().size(), score);
            } else downUp = down;
            ef.add(downUp);
            rollback();


            down();
            if (hasBoardChanged()) {
                downDown = new MoveEfficiency(getEmptyTiles().size(), score);
            } else downDown = down;
            ef.add(downDown);
            rollback();
        }
        rollback();

        Collections.sort(ef);
        Collections.reverse(ef);


        if (ef.get(0).equals(leftLeft) || ef.get(0).equals(leftRight) ||
                ef.get(0).equals(leftUp) || ef.get(0).equals(leftDown)) {
            left();
        } else if (ef.get(0).equals(rightLeft) || ef.get(0).equals(rightRight) ||
                ef.get(0).equals(rightUp) || ef.get(0).equals(rightDown)) {
            right();
        } else if (ef.get(0).equals(upLeft) || ef.get(0).equals(upRight) ||
                ef.get(0).equals(upUp) || ef.get(0).equals(upDown)) {
            up();
        } else if (ef.get(0).equals(downLeft) || ef.get(0).equals(downRight) ||
                ef.get(0).equals(downUp) || ef.get(0).equals(downDown)) {
            down();
        }
        for (MoveEfficiency m : ef) {
            System.out.println(m.getNumberOfEmptyTiles() + " " + m.getScore());
        }
        ef.clear();

    }



    // интеллектуальная реализация выбора эффективного хода из возможных
    public ArrayList<MoveEfficiency> subMove() {
        ArrayList<MoveEfficiency> ef = new ArrayList<>();

        MoveEfficiency left = new MoveEfficiency(-1, 0);
        MoveEfficiency leftLeft = new MoveEfficiency(-1, 0);
        MoveEfficiency leftRight = new MoveEfficiency(-1, 0);
        MoveEfficiency leftUp = new MoveEfficiency(-1, 0);
        MoveEfficiency leftDown = new MoveEfficiency(-1, 0);

        MoveEfficiency right = new MoveEfficiency(-1, 0);
        MoveEfficiency rightLeft = new MoveEfficiency(-1, 0);
        MoveEfficiency rightRight = new MoveEfficiency(-1, 0);
        MoveEfficiency rightUp = new MoveEfficiency(-1, 0);
        MoveEfficiency rightDown = new MoveEfficiency(-1, 0);

        MoveEfficiency up = new MoveEfficiency(-1, 0);
        MoveEfficiency upLeft = new MoveEfficiency(-1, 0);
        MoveEfficiency upRight = new MoveEfficiency(-1, 0);
        MoveEfficiency upUp = new MoveEfficiency(-1, 0);
        MoveEfficiency upDown = new MoveEfficiency(-1, 0);

        MoveEfficiency down = new MoveEfficiency(-1, 0);
        MoveEfficiency downLeft = new MoveEfficiency(-1, 0);
        MoveEfficiency downRight = new MoveEfficiency(-1, 0);
        MoveEfficiency downUp = new MoveEfficiency(-1, 0);
        MoveEfficiency downDown = new MoveEfficiency(-1, 0);

        left();
        if (hasBoardChanged()) {
            left = new MoveEfficiency(getEmptyTiles().size(), score);
            left();
            if (hasBoardChanged()) {
                leftLeft = new MoveEfficiency(getEmptyTiles().size(), score);
            } else leftLeft = left;
            ef.add(leftLeft);
            rollback();

            right();
            if (hasBoardChanged()) {
                leftRight = new MoveEfficiency(getEmptyTiles().size(), score);
            } else leftRight = left;
            ef.add(leftRight);
            rollback();

            up();
            if (hasBoardChanged()) {
                leftUp = new MoveEfficiency(getEmptyTiles().size(), score);
            } else leftUp = left;
            ef.add(leftUp);
            rollback();

            down();
            if (hasBoardChanged()) {
                leftDown = new MoveEfficiency(getEmptyTiles().size(), score);
            } else leftDown = left;
            ef.add(leftDown);
            rollback();
        }
        rollback();


        right();
        if (hasBoardChanged()) {
            right = new MoveEfficiency(getEmptyTiles().size(), score);
            left();
            if (hasBoardChanged()) {
                rightLeft = new MoveEfficiency(getEmptyTiles().size(), score);
            } else rightLeft = right;
            ef.add(rightLeft);
            rollback();

            right();
            if (hasBoardChanged()) {
                rightRight = new MoveEfficiency(getEmptyTiles().size(), score);
            } else rightRight = right;
            ef.add(rightRight);
            rollback();

            up();
            if (hasBoardChanged()) {
                rightUp = new MoveEfficiency(getEmptyTiles().size(), score);
            } else rightUp = right;
            ef.add(rightUp);
            rollback();

            down();
            if (hasBoardChanged()) {
                rightDown = new MoveEfficiency(getEmptyTiles().size(), score);
            } else rightDown = right;
            ef.add(rightDown);
            rollback();
        }
        rollback();


        up();
        if (hasBoardChanged()) {
            up = new MoveEfficiency(getEmptyTiles().size(), score);
            left();
            if (hasBoardChanged()) {
                upLeft = new MoveEfficiency(getEmptyTiles().size(), score);
            } else upLeft = up;
            ef.add(upLeft);
            rollback();


            right();
            if (hasBoardChanged()) {
                upRight = new MoveEfficiency(getEmptyTiles().size(), score);
            } else upRight = up;
            ef.add(upRight);
            rollback();


            up();
            if (hasBoardChanged()) {
                upUp = new MoveEfficiency(getEmptyTiles().size(), score);
            } else upUp = up;
            ef.add(upUp);
            rollback();


            down();
            if (hasBoardChanged()) {
                upDown = new MoveEfficiency(getEmptyTiles().size(), score);
            } else upDown = up;
            ef.add(upDown);
            rollback();
        }
        rollback();


        down();
        if (hasBoardChanged()) {
            down = new MoveEfficiency(getEmptyTiles().size(), score);
            left();
            if (hasBoardChanged()) {
                downLeft = new MoveEfficiency(getEmptyTiles().size(), score);
            } else downLeft = down;
            ef.add(downLeft);
            rollback();


            right();
            if (hasBoardChanged()) {
                downRight = new MoveEfficiency(getEmptyTiles().size(), score);
            } else downRight = down;
            ef.add(downRight);
            rollback();


            up();
            if (hasBoardChanged()) {
                downUp = new MoveEfficiency(getEmptyTiles().size(), score);
            } else downUp = down;
            ef.add(downUp);
            rollback();


            down();
            if (hasBoardChanged()) {
                downDown = new MoveEfficiency(getEmptyTiles().size(), score);
            } else downDown = down;
            ef.add(downDown);
            rollback();
        }
        rollback();

        Collections.sort(ef);
        Collections.reverse(ef);

        if (ef.size()==0){
            ef.add(new MoveEfficiency(-1, 0));
        }

        return ef;
    }

    public ArrayList<MoveEfficiency> intelMove() {
        ArrayList<MoveEfficiency> superEf = new ArrayList<>();
        ArrayList<MoveEfficiency> subLeft = new ArrayList<>();
        ArrayList<MoveEfficiency> subRight = new ArrayList<>();
        ArrayList<MoveEfficiency> subUp = new ArrayList<>();
        ArrayList<MoveEfficiency> subDown = new ArrayList<>();

        left();
        if (hasBoardChanged()) subLeft = subMove();
        else subLeft.add(new MoveEfficiency(-1, 0));
        rollback();

        right();
        if (hasBoardChanged()) subRight = subMove();
        else subRight.add(new MoveEfficiency(-1, 0));
        rollback();

        up();
        if (hasBoardChanged()) subUp = subMove();
        else subUp.add(new MoveEfficiency(-1, 0));
        rollback();

        down();
        if (hasBoardChanged()) subDown = subMove();
        else subDown.add(new MoveEfficiency(-1, 0));
        rollback();

        MoveEfficiency left = subLeft.get(0);
        MoveEfficiency right = subRight.get(0);
        MoveEfficiency up = subUp.get(0);
        MoveEfficiency down = subDown.get(0);

        superEf.add(left);
        superEf.add(right);
        superEf.add(up);
        superEf.add(down);

        Collections.sort(superEf);
        Collections.reverse(superEf);

        if (superEf.size()==0){
            superEf.add(new MoveEfficiency(-1, 0));
        }

        return superEf;
    }


}
