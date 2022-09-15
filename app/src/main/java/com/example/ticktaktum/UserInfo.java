package com.example.ticktaktum;

import java.util.ArrayList;

public class UserInfo {
    String username1 , username2;
    Boolean taken;
    String turn;
    String user1_again;
    String user2_again;
    String won;
    ArrayList<String> moves;

    public UserInfo(String username1, String username2, Boolean taken, ArrayList<String> moves , String turn , String won , String user1_again , String user2_again) {
        this.username1 = username1;
        this.username2 = username2;
        this.taken = taken;
        this.moves = moves;
        this.turn = turn;
        this.user1_again = user1_again;
        this.user2_again = user2_again;
        this.won = won;
    }

    public String getWon() {
        return won;
    }

    public void setWon(String won) {
        this.won = won;
    }

    public String getUser1_again() {
        return user1_again;
    }

    public void setUser1_again(String user1_again) {
        this.user1_again = user1_again;
    }

    public String getUser2_again() {
        return user2_again;
    }

    public void setUser2_again(String user2_again) {
        this.user2_again = user2_again;
    }

    public String getUsername1() {
        return username1;
    }

    public void setUsername1(String username1) {
        this.username1 = username1;
    }

    public String getUsername2() {
        return username2;
    }

    public Boolean getTaken() {
        return taken;
    }

    public void setTaken(Boolean taken) {
        this.taken = taken;
    }

    public void setUsername2(String username2) {
        this.username2 = username2;
    }

    public ArrayList<String> getMoves() {
        return moves;
    }

    public String getTurn() {
        return turn;
    }

    public void setTurn(String turn) {
        this.turn = turn;
    }

    public void setMessages(ArrayList<String> moves) {
        this.moves = moves;
    }
}
