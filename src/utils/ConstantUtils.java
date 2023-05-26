package utils;

import enums.modelsEnums.UserRole;
import storage.Database;
import storage.models.*;

public class ConstantUtils {
    public static Users authorizedUser = Database.getInstance().select(Users.builder().role(UserRole.Administrator.ordinal()).build()).first();
    public static Users selectedUser;
    public static Animal selectedAnimal;
    public static Cure selectedCure;
    public static Visit selectedVisit;
    public static Vaccination selectedVaccination;
    public static int wrongAttemptsCounter = 0;
    public static int possibleAttempts = 3;
    public static int showPasswordTime = 5000;
}
