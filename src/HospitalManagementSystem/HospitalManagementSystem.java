package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class HospitalManagementSystem {
    private static final String url = "jdbc:mysql://localhost:3306/hospital";
    private static final String username = "root";
    private static final String password = "root";

    public static void main(String[] args) {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        Scanner scanner = new Scanner(System.in);
        try{
            Connection connection = DriverManager.getConnection(url, username, password);
            Patient patient = new Patient(connection,scanner);
            Doctor doctor = new Doctor(connection);
            while(true){
                System.out.println("Hospital Management System ");
                System.out.println("1. Add Patient");
                System.out.println("2. View Patient ");
                System.out.println("3. View Doctors");
                System.out.println("4. Book Appoitment ");
                System.out.println("5. Exit");
                System.out.println("Enter your Choice ");
                int choice = scanner.nextInt();

                switch (choice){
                    case 1:
                        //Add Patient
                        patient.addPatient();
                        System.out.println();
                        break;
                    case 2:
                        //View Patient
                        patient.viewPatients();
                        System.out.println();
                        break;
                    case 3:
                        //View Doctor
                        doctor.viewDoctorss();
                        System.out.println();
                        break;
                    case 4:
                        //Book Appoitment
                        bookAppointment(patient,doctor,connection,scanner);
                        System.out.println();
                        break;
                    case 5:
                        System.out.println("Thank you for using Hospital Management System");
                        return;
                    default:
                        System.out.println("Enter Valid Choice");
                        break;
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void bookAppointment(Patient patient, Doctor doctor,Connection connection, Scanner scanner){
        System.out.println("Enetr patient ID: ");
        int patientId = scanner.nextInt();
        System.out.println("Enter Doctor Id: ");
        int doctorId = scanner.nextInt();
        System.out.println("Enter Appointment date (yyyy-mm-dd)");
        String appointmentDate = scanner.next();
        if (patient.getPatientId(patientId) && doctor.getDoctorById((doctorId))) {
            if(checkDoctorAvailability(doctorId, appointmentDate, connection)){
                String appointmentQuery = "INSERT INTO appointments(patient_id, doctor_id, appointment_date) VALUES(?,?,?)";
                try{
                    PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
                    preparedStatement.setInt(1,patientId);
                    preparedStatement.setInt(2,doctorId);
                    preparedStatement.setString(3,appointmentDate);
                    int rowsAffected = preparedStatement.executeUpdate();
                    if(rowsAffected>0){
                        System.out.println("Appointment Booked !");
                    }else{
                        System.out.println("Failed to bok Appointment");
                    }
                }catch (SQLException e){
                    e.printStackTrace();
                }
            }else{
                System.out.println("Doctor is not available");
            }
        }else {
            System.out.println("Either doctor and or patient doesn't exist !!!");
        }
    }
    public static boolean checkDoctorAvailability(int docterId, String appointmentDate, Connection connection){
        String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,docterId);
            preparedStatement.setString(2,appointmentDate);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                int count = resultSet.getInt(1);
                if (count == 0){
                    return true;
                }else {
                    return false;
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
}
