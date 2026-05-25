package com.example.fitnessapp.server;

import com.example.fitnessapp.dao.ClubDAO;
import com.example.fitnessapp.model.Club;

import java.io.PrintWriter;
import java.util.List;

public class ClubCommandHandler {

    private final ClubDAO clubDao;

    public ClubCommandHandler(ClubDAO clubDao) {
        this.clubDao = clubDao;
    }

    public boolean handle(String command, String[] tokens, PrintWriter out) {
        if ("ADD_CLUB".equals(command)) {
            return handleAddClub(tokens, out);
        }
        if ("GET_CLUBS".equals(command)) {
            return handleGetClubs(tokens, out);
        }
        return false;
    }

    private boolean handleAddClub(String[] tokens, PrintWriter out) {
        try {
            // Format: ADD_CLUB;name;address;openingHours
            if (tokens.length < 4) {
                out.println("ADD_CLUB_ERROR;Za malo danych");
                return true;
            }

            String name = tokens[1];
            String address = tokens[2];
            String openingHours = tokens[3];

            if (name == null || name.trim().isEmpty()) {
                out.println("ADD_CLUB_ERROR;Nazwa jest wymagana");
                return true;
            }
            if (address == null || address.trim().isEmpty()) {
                out.println("ADD_CLUB_ERROR;Adres jest wymagany");
                return true;
            }

            Club club = new Club(name, address, openingHours);
            clubDao.save(club);

            out.println("ADD_CLUB_OK;Klub dodany;" + club.getId());
            return true;
        } catch (Exception e) {
            out.println("ADD_CLUB_ERROR;Blad dodawania klubu: " + e.getMessage());
            return true;
        }
    }

    private boolean handleGetClubs(String[] tokens, PrintWriter out) {
        List<Club> list = clubDao.findAll();

        StringBuilder sb = new StringBuilder("CLUBS_OK");
        for (Club c : list) {
            sb.append(";")
                    .append(c.getId()).append(",")
                    .append(c.getName()).append(",")
                    .append(c.getAddress()).append(",")
                    .append(c.getOpeningHours());
        }
        out.println(sb.toString());
        return true;
    }
}

