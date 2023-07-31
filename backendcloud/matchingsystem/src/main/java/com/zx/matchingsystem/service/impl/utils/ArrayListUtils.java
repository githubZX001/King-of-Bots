package com.zx.matchingsystem.service.impl.utils;

import java.util.ArrayList;
import java.util.List;


public class ArrayListUtils {
    public static ArrayList<Player> removePlayerArray(ArrayList<Player> players, Integer userId){
        ArrayList<Player> newPlayers = new ArrayList<>();
        for(Player player : players){
            if(!player.getUserId().equals(userId)){
                newPlayers.add(player);
            }
        }
        players = newPlayers;
        return players;
    }
}
