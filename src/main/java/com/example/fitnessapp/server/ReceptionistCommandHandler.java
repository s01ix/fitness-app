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

    public ReceptionistCommandHandler(PassTypeDAO passTypeDao,
                                      GymPassDAO gymPassDao,
                                      PaymentDAO paymentDao,
                                      GroupClassDAO groupClassDao,
                                      ReservationDAO reservationDao,
                                      GymUserDAO userDao) {
        this.passTypeDao = passTypeDao;
        this.gymPassDao = gymPassDao;
        this.paymentDao = paymentDao;
        this.groupClassDao = groupClassDao;
        this.reservationDao = reservationDao;
        this.userDao = userDao;
    }

    public boolean handle(String command, String[] tokens, PrintWriter out) {
        switch (command) {
            case "GET_PASS_TYPES":
                return handleGetPassTypes(tokens, out);
            case "SELL_PASS":
                return handleSellPass(tokens, out);
            case "GET_CLIENT_PASSES":
                return handleGetClientPasses(tokens, out);
            case "GET_GROUP_CLASSES":
                return handleGetGroupClasses(tokens, out);
            case "CREATE_RESERVATION":
                return handleCreateReservation(tokens, out);
            case "CANCEL_RESERVATION":
                return handleCancelReservation(tokens, out);
            case "SEARCH_CLIENT":
                return handleSearchClient(tokens, out);
            case "GET_CLIENT_RESERVATIONS":
                return handleGetClientReservations(tokens, out);
            default:
                return false;
        }
    }

    private boolean handleGetPassTypes(String[] tokens, PrintWriter out) {
        try {
            List<PassType> passTypes = passTypeDao.findAll();
            StringBuilder sb = new StringBuilder("PASS_TYPES_OK");

            for (PassType pt : passTypes) {
                sb.append(";")
                        .append(pt.getId()).append(",")
                        .append(pt.getName()).append(",")
                        .append(pt.getBasePrice());
            }

            out.println(sb.toString());
            return true;
        } catch (Exception e) {
            out.println("PASS_TYPES_ERROR;" + e.getMessage());
            return true;
        }
    }

    private boolean handleSellPass(String[] tokens, PrintWriter out) {
        try {
            if (tokens.length < 4) {
                out.println("SELL_PASS_ERROR;Za mało danych");
                return true;
            }

            int clientId = Integer.parseInt(tokens[1]);
            int passTypeId = Integer.parseInt(tokens[2]);
            String paymentMethod = tokens[3];

            Optional<PassType> ptOpt = passTypeDao.findById(passTypeId);
            if (!ptOpt.isPresent()) {
                out.println("SELL_PASS_ERROR;Nieprawidłowy typ karnetu");
                return true;
            }

            PassType passType = ptOpt.get();
            BigDecimal price = passType.getBasePrice();

            boolean success = gymPassDao.purchasePass(clientId, passTypeId, price, paymentMethod);

            if (success) {
                out.println("SELL_PASS_OK;Karnet sprzedany pomyślnie");
            } else {
                out.println("SELL_PASS_ERROR;Błąd transakcji");
            }

            return true;
        } catch (Exception e) {
            out.println("SELL_PASS_ERROR;" + e.getMessage());
            return true;
        }
    }

    private boolean handleGetClientPasses(String[] tokens, PrintWriter out) {
        try {
            if (tokens.length < 2) {
                out.println("PASSES_ERROR;Brak ID klienta");
                return true;
            }

            int clientId = Integer.parseInt(tokens[1]);
            List<GymPass> passes = gymPassDao.findActiveByUserId(clientId);

            StringBuilder sb = new StringBuilder("PASSES_OK");
            for (GymPass pass : passes) {
                String passTypeName = passTypeDao.findById(pass.getPassTypeId())
                        .map(PassType::getName)
                        .orElse("Nieznany");

                sb.append(";")
                        .append("ID: ").append(pass.getId())
                        .append(" | Typ: ").append(passTypeName)
                        .append(" | Cena: ").append(pass.getPrice()).append(" zł")
                        .append(" | Zakup: ").append(pass.getPurchaseDate())
                        .append(" | Wygasa: ").append(pass.getExpirationDate())
                        .append(" | Status: ").append(pass.getStatus());
            }

            out.println(sb.toString());
            return true;
        } catch (Exception e) {
            out.println("PASSES_ERROR;" + e.getMessage());
            return true;
        }
    }

    private boolean handleGetGroupClasses(String[] tokens, PrintWriter out) {
        try {
            List<GroupClass> classes = groupClassDao.findAll();
            StringBuilder sb = new StringBuilder("CLASSES_OK");

            for (GroupClass gc : classes) {
                int bookedCount = reservationDao.countConfirmedReservations(gc.getId());

                sb.append(";")
                        .append(gc.getId()).append(",")
                        .append(gc.getName()).append(",")
                        .append(gc.getScheduleTime().toString().replace('T', ' ')).append(",")
                        .append(gc.getCapacity()).append(",")
                        .append(bookedCount).append(",")
                        .append(gc.getStatus());
            }

            out.println(sb.toString());
            return true;
        } catch (Exception e) {
            out.println("CLASSES_ERROR;" + e.getMessage());
            return true;
        }
    }

    private boolean handleCreateReservation(String[] tokens, PrintWriter out) {
        try {
            if (tokens.length < 3) {
                out.println("RESERVATION_ERROR;Za mało danych");
                return true;
            }
            int clientId = Integer.parseInt(tokens[1]);
            int classId = Integer.parseInt(tokens[2]);
            if (!reservationDao.hasAvailableSpots(classId)) {
                out.println("RESERVATION_ERROR;Brak wolnych miejsc");
                return true;
            }
            Reservation res = new Reservation(
                    clientId,
                    classId,
                    0,
                    LocalDate.now(),
                    "CONFIRMED"
            );

            reservationDao.save(res);

            out.println("RESERVATION_OK;Rezerwacja utworzona");
            return true;
        } catch (Exception e) {
            out.println("RESERVATION_ERROR;" + e.getMessage());
            return true;
        }
    }

    private boolean handleCancelReservation(String[] tokens, PrintWriter out) {
        try {
            if (tokens.length < 2) {
                out.println("CANCEL_ERROR;Brak ID rezerwacji");
                return true;
            }

            int resId = Integer.parseInt(tokens[1]);
            reservationDao.cancel(resId);

            out.println("CANCEL_OK;Rezerwacja anulowana");
            return true;
        } catch (Exception e) {
            out.println("CANCEL_ERROR;" + e.getMessage());
            return true;
        }
    }

    private boolean handleSearchClient(String[] tokens, PrintWriter out) {
        try {
            if (tokens.length < 2) {
                out.println("SEARCH_ERROR;Brak kryterium wyszukiwania");
                return true;
            }

            String searchTerm = tokens[1];

            Optional<GymUser> byEmail = userDao.findByEmail(searchTerm);
            if (byEmail.isPresent()) {
                GymUser user = byEmail.get();
                out.println(String.format("CLIENT_FOUND;%d;%s;%s;%s",
                        user.getId(), user.getFirstName(), user.getLastName(), user.getEmail()));
                return true;
            }

            List<GymUser> allUsers = userDao.findAll();
            for (GymUser user : allUsers) {
                if (searchTerm.equals(user.peselProperty().get())) {
                    out.println(String.format("CLIENT_FOUND;%d;%s;%s;%s",
                            user.getId(), user.getFirstName(), user.getLastName(), user.getEmail()));
                    return true;
                }
            }

            out.println("CLIENT_NOT_FOUND;Nie znaleziono klienta");
            return true;
        } catch (Exception e) {
            out.println("SEARCH_ERROR;" + e.getMessage());
            return true;
        }
    }

    private boolean handleGetClientReservations(String[] tokens, PrintWriter out) {
        try {
            if (tokens.length < 2) {
                out.println("RESERVATIONS_ERROR;Brak ID klienta");
                return true;
            }

            int clientId = Integer.parseInt(tokens[1]);
            List<Reservation> reservations = reservationDao.findByClientId(clientId);

            StringBuilder sb = new StringBuilder("RESERVATIONS_OK");
            for (Reservation res : reservations) {
                String className = groupClassDao.findById(res.getClassId())
                        .map(GroupClass::getName)
                        .orElse("Nieznane");

                sb.append(";")
                        .append("ID: ").append(res.getId())
                        .append(" | Zajęcia: ").append(className)
                        .append(" | Data: ").append(res.getReservationDate())
                        .append(" | Status: ").append(res.getStatus());
            }

            out.println(sb.toString());
            return true;
        } catch (Exception e) {
            out.println("RESERVATIONS_ERROR;" + e.getMessage());
            return true;
        }
    }
}