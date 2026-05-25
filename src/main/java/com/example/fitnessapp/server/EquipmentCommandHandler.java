package com.example.fitnessapp.server;

import com.example.fitnessapp.dao.EquipmentDAO;
import com.example.fitnessapp.model.Equipment;

import java.io.PrintWriter;
import java.util.List;

public class EquipmentCommandHandler {

    private final EquipmentDAO equipmentDao;

    public EquipmentCommandHandler(EquipmentDAO equipmentDao) {
        this.equipmentDao = equipmentDao;
    }

    public boolean handle(String command, String[] tokens, PrintWriter out) {
        if ("ADD_EQUIPMENT".equals(command)) {
            return handleAddEquipment(tokens, out);
        }
        if ("GET_EQUIPMENT".equals(command)) {
            return handleGetEquipment(tokens, out);
        }
        return false;
    }

    private boolean handleAddEquipment(String[] tokens, PrintWriter out) {
        try {
            // Format: ADD_EQUIPMENT;clubId;name;status;lastInspectionDate(yyyy-MM-dd)
            if (tokens.length < 5) {
                out.println("ADD_EQUIPMENT_ERROR;Za malo danych");
                return true;
            }

            int clubId = Integer.parseInt(tokens[1]);
            String name = tokens[2];
            String status = tokens[3];
            java.time.LocalDate lastInspectionDate = java.time.LocalDate.parse(tokens[4]);

            if (name == null || name.trim().isEmpty()) {
                out.println("ADD_EQUIPMENT_ERROR;Nazwa jest wymagana");
                return true;
            }
            if (status == null || status.trim().isEmpty()) {
                out.println("ADD_EQUIPMENT_ERROR;Status jest wymagany");
                return true;
            }

            Equipment equipment = new Equipment(0, clubId, name, status, lastInspectionDate);
            equipmentDao.save(equipment);

            out.println("ADD_EQUIPMENT_OK;Sprzet dodany;" + equipment.getId());
            return true;
        } catch (Exception e) {
            out.println("ADD_EQUIPMENT_ERROR;Blad dodawania sprzetu: " + e.getMessage());
            return true;
        }
    }

    private boolean handleGetEquipment(String[] tokens, PrintWriter out) {
        List<Equipment> list;
        if (tokens.length > 1 && tokens[1] != null && !tokens[1].trim().isEmpty()) {
            int clubId = Integer.parseInt(tokens[1]);
            list = equipmentDao.findByClubId(clubId);
        } else {
            list = equipmentDao.findAll();
        }

        StringBuilder sb = new StringBuilder("EQUIPMENT_OK");
        for (Equipment e : list) {
            sb.append(";")
                    .append(e.getId()).append(",")
                    .append(e.getClubId()).append(",")
                    .append(e.getName()).append(",")
                    .append(e.getStatus()).append(",")
                    .append(e.getLastInspectionDate());
        }
        out.println(sb.toString());
        return true;
    }
}
