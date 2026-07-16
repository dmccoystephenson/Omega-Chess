// Immutable POJO — Java records require JDK 16+; this project targets JDK 7/8.
package com.omegaChess.protocol.messages;

public class ProfileDataSuccessResponse {

    private final boolean success;
    private final String nickname;
    private final int gamesWon;
    private final int gamesLost;
    private final int gamesTied;

    public ProfileDataSuccessResponse(boolean success, String nickname, int gamesWon, int gamesLost, int gamesTied) {
        this.success = success;
        this.nickname = nickname;
        this.gamesWon = gamesWon;
        this.gamesLost = gamesLost;
        this.gamesTied = gamesTied;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getNickname() {
        return nickname;
    }

    public int getGamesWon() {
        return gamesWon;
    }

    public int getGamesLost() {
        return gamesLost;
    }

    public int getGamesTied() {
        return gamesTied;
    }
}
