package com.example.fitnessapp.server;

import com.example.fitnessapp.dao.*;
import com.example.fitnessapp.model.*;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ReceptionistCommandHandler {

    private final PassTypeDAO passTypeDao;
    private final GymPassDAO gymPassDao;
    private final PaymentDAO paymentDao;
    private final GroupClassDAO groupClassDao;
    private final ReservationDAO reservationDao;
    private final GymUserDAO userDao;
    private final ClubDAO clubDao;
    private final MessageDAO messageDao;

    public ReceptionistCommandHandler(PassTypeDAO passTypeDao, GymPassDAO gymPassDao, PaymentDAO paymentDao,
                                      GroupClassDAO groupClassDao, ReservationDAO reservationDao, GymUserDAO userDao,
                                      ClubDAO clubDao, MessageDAO messageDao) {
        this.passTypeDao = passTypeDao;
        this.gymPassDao = gymPassDao;
        this.paymentDao = paymentDao;
        this.groupClassDao = groupClassDao;
        this.reservationDao = reservationDao;
        this.userDao = userDao;
        this.clubDao = clubDao;
        this.messageDao = messageDao;
    }

    public boolean handle(String command, String[] tokens, PrintWriter out) {
        switch (command) {
            case "GET_PASS_TYPES": return handleGetPassTypes(out);
            case "SELL_PASS": return handleSellPass(tokens, out);
            case "GET_CLIENT_PASSES": return handleGetClientPasses(tokens, out);
            case "GET_GROUP_CLASSES": return handleGetGroupClasses(out);
            case "CREATE_RESERVATION": return handleCreateReservation(tokens, out);
            case "CANCEL_RESERVATION": return handleCancelReservation(tokens, out);
            case "SEARCH_CLIENT": return handleSearchClient(tokens, out);
            case "GET_CLIENT_RESERVATIONS": return handleGetClientReservations(tokens, out);
            case "VERIFY_ENTRY": return handleVerifyEntry(tokens, out);
            case "GET_CLUB_INFO": return handleGetClubInfo(out);
            case "LOG_COMPLAINT": return handleLogComplaint(tokens, out);
            default: return false;
        }
    }

    private boolean handleGetPassTypes(PrintWriter out) {
        try {
            List<PassType> pt = passTypeDao.findAll();
            if (pt.isEmpty()) {
                out.println("PASS_TYPES_ERROR;Brak dostępnych karnetów w bazie.");
                return true;
            }
            StringBuilder sb = new StringBuilder("PASS_TYPES_OK");
            for (PassType p : pt) {
                sb.append(";").append(p.getId()).append(",").append(p.getName()).append(",").append(p.getBasePrice());
            }
            out.println(sb.toString()); 
            return true;
        } catch (Exception e) { out.println("PASS_TYPES_ERROR;Błąd bazy: " + e.getMessage()); return true; }
    }

    private boolean handleSellPass(String[] tokens, PrintWriter out) {
        try {
            if (tokens.length < 4) {
                out.println("SELL_PASS_ERROR;Brak pełnych danych do sprzedaży.");
                return true;
            }
            int userId = Integer.parseInt(tokens[1]);
            int passTypeId = Integer.parseInt(tokens[2]);
            String paymentMethod = tokens[3];

            Optional<PassType> ptOpt = passTypeDao.findById(passTypeId);
            if (!ptOpt.isPresent()) {
                out.println("SELL_PASS_ERROR;Wybrany karnet nie istnieje w bazie.");
                return true;
            }

            BigDecimal price = ptOpt.get().getBasePrice();
            boolean success = gymPassDao.purchasePass(userId, passTypeId, price, paymentMethod);
            
            if (success) {
                out.println("SELL_PASS_OK;Karnet został pomyślnie sprzedany i przypisany do klienta.");
            } else {
                out.println("SELL_PASS_ERROR;Odrzucono transakcję przez bazę danych.");
            }
            return true;
        } catch (Exception e) { out.println("SELL_PASS_ERROR;Błąd serwera: " + e.getMessage()); return true; }
    }

    private boolean handleGetClientPasses(String[] tokens, PrintWriter out) {
        try {
            int userId = Integer.parseInt(tokens[1]);
            List<GymPass> passes = gymPassDao.findActiveByUserId(userId);
            
            if (passes.isEmpty()) {
                out.println("PASSES_OK;Brak aktywnych karnetów.");
                return true;
            }

            StringBuilder sb = new StringBuilder("PASSES_OK");
            for (GymPass p : passes) {
                String passName = passTypeDao.findById(p.getPassTypeId()).map(PassType::getName).orElse("Nieznany karnet");
                sb.append(";[ID: ").append(p.getId()).append("] ").append(passName)
                  .append(" | Wygasa: ").append(p.getExpirationDate());
            }
            out.println(sb.toString()); 
            return true;
        } catch (Exception e) { out.println("PASSES_ERROR;Błąd: " + e.getMessage()); return true; }
    }

    private boolean handleVerifyEntry(String[] tokens, PrintWriter out) {
        try {
            int clientId = Integer.parseInt(tokens[1]);
            List<GymPass> passes = gymPassDao.findActiveByUserId(clientId);
            if (passes.isEmpty()) {
                out.println("VERIFY_DENIED;Odmowa dostępu. Klient nie posiada aktywnego karnetu!");
            } else {
                out.println("VERIFY_GRANTED;Dostęp przyznany. Karnet aktywny do: " + passes.get(0).getExpirationDate());
            }
            return true;
        } catch (Exception e) { out.println("VERIFY_ERROR;Błąd weryfikacji: " + e.getMessage()); return true; }
    }

    private boolean handleGetGroupClasses(PrintWriter out) {
        try {
            List<GroupClass> classes = groupClassDao.findAll();
            if (classes.isEmpty()) {
                out.println("CLASSES_ERROR;Brak zaplanowanych zajęć w bazie.");
                return true;
            }
            StringBuilder sb = new StringBuilder("CLASSES_OK");
            for (GroupClass gc : classes) {
                int booked = reservationDao.countConfirmedReservations(gc.getId());
                sb.append(";").append(gc.getId()).append(",")
                  .append(gc.getName()).append(",")
                  .append(gc.getScheduleTime()).append(",")
                  .append(gc.getCapacity()).append(",")
                  .append(booked).append(",")
                  .append(gc.getStatus());
            }
            out.println(sb.toString()); 
            return true;
        } catch (Exception e) { out.println("CLASSES_ERROR;Błąd bazy: " + e.getMessage()); return true; }
    }

    private boolean handleCreateReservation(String[] tokens, PrintWriter out) {
        try {
            int clientId = Integer.parseInt(tokens[1]);
            int classId = Integer.parseInt(tokens[2]);
            
            if (!reservationDao.hasAvailableSpots(classId)) {
                out.println("RESERVATION_ERROR;Brak wolnych miejsc na te zajęcia.");
                return true;
            }
            
            reservationDao.save(new Reservation(0, clientId, classId, 0, LocalDate.now(), "CONFIRMED"));
            out.println("RESERVATION_OK;Klient został pomyślnie zapisany na zajęcia."); 
            return true;
        } catch (Exception e) { out.println("RESERVATION_ERROR;Błąd rezerwacji: " + e.getMessage()); return true; }
    }

    private boolean handleCancelReservation(String[] tokens, PrintWriter out) {
        try {
            int resId = Integer.parseInt(tokens[1]);
            reservationDao.cancel(resId);
            out.println("CANCEL_OK;Rezerwacja została anulowana."); 
            return true;
        } catch (Exception e) { out.println("CANCEL_ERROR;Błąd anulowania: " + e.getMessage()); return true; }
    }

    private boolean handleSearchClient(String[] tokens, PrintWriter out) {
        try {
            if (tokens.length < 2 || tokens[1].trim().isEmpty()) {
                out.println("SEARCH_ERROR;Proszę podać dane do wyszukiwania.");
                return true;
            }
            String term = tokens[1].trim();
            Optional<GymUser> u = userDao.findByEmail(term);
            if(u.isPresent()) { 
                out.println("CLIENT_FOUND;" + u.get().getId() + ";" + u.get().getFirstName() + ";" + u.get().getLastName() + ";" + u.get().getEmail()); 
                return true; 
            }
            for(GymUser user : userDao.findAll()) {
                if(user.peselProperty().get() != null && user.peselProperty().get().equals(term)) { 
                    out.println("CLIENT_FOUND;" + user.getId() + ";" + user.getFirstName() + ";" + user.getLastName() + ";" + user.getEmail()); 
                    return true; 
                }
            }
            out.println("CLIENT_NOT_FOUND;W bazie nie istnieje klient z takim PESELem lub emailem."); 
            return true;
        } catch (Exception e) { out.println("SEARCH_ERROR;Błąd wyszukiwania: " + e.getMessage()); return true; }
    }

    private boolean handleGetClientReservations(String[] tokens, PrintWriter out) {
        try {
            int clientId = Integer.parseInt(tokens[1]);
            List<Reservation> res = reservationDao.findByClientId(clientId);
            if (res.isEmpty()) {
                out.println("RESERVATIONS_OK;Ten klient nie posiada żadnych rezerwacji.");
                return true;
            }
            StringBuilder sb = new StringBuilder("RESERVATIONS_OK");
            for(Reservation r : res) {
                sb.append(";ID: ").append(r.getId()).append(" | Data: ").append(r.getReservationDate()).append(" | Status: ").append(r.getStatus());
            }
            out.println(sb.toString()); 
            return true;
        } catch (Exception e) { out.println("RESERVATIONS_ERROR;Błąd bazy: " + e.getMessage()); return true; }
    }

    private boolean handleGetClubInfo(PrintWriter out) {
        try {
            List<Club> clubs = clubDao.findAll();
            if (!clubs.isEmpty()) {
                Club c = clubs.get(0);
                out.println("CLUB_INFO_OK;" + c.getName() + ";" + c.getAddress() + ";" + c.getOpeningHours());
            } else {
                out.println("CLUB_INFO_ERROR;Brak informacji o klubie w bazie.");
            }
            return true;
        } catch (Exception e) { out.println("CLUB_INFO_ERROR;Błąd pobierania danych: " + e.getMessage()); return true; }
    }

    private boolean handleLogComplaint(String[] tokens, PrintWriter out) {
        try {
            if (tokens.length < 3) {
                out.println("COMPLAINT_ERROR;Treść zgłoszenia nie może być pusta.");
                return true;
            }
            int clientId = Integer.parseInt(tokens[1]);
            String content = "ZGŁOSZENIE Z RECEPCJI: " + tokens[2];
            Message complaint = new Message(0, clientId, 1, content, LocalDateTime.now());
            messageDao.save(complaint);
            out.println("COMPLAINT_OK;Zgłoszenie zostało pomyślnie zarejestrowane w systemie.");
            return true;
        } catch (Exception e) { out.println("COMPLAINT_ERROR;Błąd zapisu zgłoszenia: " + e.getMessage()); return true; }
    }
}