/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package org.example;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Main Method:
It presents a menu to the user with options to search for a customer by name, generate statistics, generate a sales report, or exit the application.
* Load Customer Data:
Reads data from the CSV file "SuperStoreOrders.csv" and stores it in a HashMap named customerData with customer names as keys and corresponding order details as values.
* Search Customer by Name:
Allows the user to search for a customer by name and displays their orders if found.
* Display Customer Orders:
Displays orders made by a specific customer.
* Generate Sales Report:
Generates a sales report in a text file named with the current date.
Redirects the standard output to the file to capture the report.
Calls the generateStatistics() method to populate the sales report.
* Generate Statistics:
Calculates various statistics such as average sales amount, best customer, customers per state, customer segment counts, total sales per year, and total sales per region.
 */

public class App {
    private static final String csvFile = "SuperStoreOrders.csv"; 
    private static HashMap<String, List<String[]>> customerData;

    public static void main(String[] args) {
        loadCustomerData();

    Scanner scanner = new Scanner(System.in);
    boolean exit = false;

    while (!exit) {
        System.out.println("\nWelcome to the CSV Reader Application!");
        System.out.println("1. Search for customer by name");
        System.out.println("2. Generate statistics");
        System.out.println("3. Generate sales report");
        System.out.println("4. Exit");
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1:
                searchCustomerByName(scanner);
                break;
            case 2:
                generateStatistics();
                break;
            case 3:
                generateSalesReport();
                break;
            case 4:
                exit = true;
                System.out.println("Exiting the application. Goodbye!");
                break;
            default:
                System.out.println("Invalid choice. Please enter a valid option.");
        }
    }
    scanner.close();
}

    private static void loadCustomerData() {
        customerData = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip the header line
                }
                String[] data = line.split(";");
                String customerName = data[6]; // Assuming customer name is at index 6
                if (!customerData.containsKey(customerName)) {
                    customerData.put(customerName, new ArrayList<>());
                }
                customerData.get(customerName).add(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void searchCustomerByName(Scanner scanner) {
        System.out.print("Enter customer name to search: ");
        String searchCustomerName = scanner.nextLine().trim();
        List<String[]> matchingCustomers = customerData.get(searchCustomerName);
        
        if (matchingCustomers != null) {
            displayCustomerOrders(searchCustomerName);
        } else {
            System.out.println("No customers found with the provided name.");
        }
    }

    private static void displayCustomerOrders(String customerName) {
        List<String[]> orders = customerData.get(customerName);
        if (orders == null || orders.isEmpty()) {
            System.out.println("No orders found for customer: " + customerName);
            return;
        }
    
        System.out.println("\nOrders made by customer: " + customerName);
        System.out.println("| Row ID | Order ID        | Order Date | Ship Date | Ship Mode   | Customer ID | Customer Name | Segment  | Country      | City         | State     | Postal Code | Region | Product ID      | Category      | Sub-Category | Product Name                               | Sales  | Quantity | Discount | Profit  |");
        
        for (String[] order : orders) {
            StringBuilder row = new StringBuilder("|");
            for (String data : order) {
                row.append(String.format(" %-8s|", data));
            }
            System.out.println(row);
        }
    }






    private static void generateSalesReport() {
    String fileName = "sales-report_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".txt";
    try (PrintWriter writer = new PrintWriter(fileName)) {
        // Redirect output to file
        System.setOut(new PrintStream(new FileOutputStream(fileName)));

        generateStatistics(); // Call the generateStatistics() method to generate the sales report

        System.out.println("\nSales report generated successfully. Saved as: " + fileName);
    } catch (FileNotFoundException e) {
        System.err.println("Error: " + e.getMessage());
    }
}
    




    
    public static void generateStatistics() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat df = new DecimalFormat("#,###.##", symbols);
    
        // Calculate average sales amount of the orders
        double totalSales = 0;
        int totalOrders = 0;
        for (List<String[]> orders : customerData.values()) {
            for (String[] order : orders) {
                try {
                    double sales = df.parse(order[16]).doubleValue(); // Assuming sales amount is at index 16
                    totalSales += sales;
                    totalOrders++;
                } catch (ParseException e) {
                    
                }
            }
        }
        double averageSales = totalSales / totalOrders;
    
        System.out.println("Average sales amount of the orders: $" + df.format(averageSales));
    
        // Find the best customer (highest total sales)
        Map<String, Double> totalSalesPerCustomer = new HashMap<>();
        for (Map.Entry<String, List<String[]>> entry : customerData.entrySet()) {
            double customerTotalSales = 0;
            for (String[] order : entry.getValue()) {
                try {
                    double sales = df.parse(order[16]).doubleValue();
                    customerTotalSales += sales;
                } catch (ParseException e) {
                    
                }
            }
            totalSalesPerCustomer.put(entry.getKey(), customerTotalSales);
        }
        String bestCustomer = Collections.max(totalSalesPerCustomer.entrySet(), Map.Entry.comparingByValue()).getKey();
        double bestCustomerTotalSales = totalSalesPerCustomer.get(bestCustomer);
        System.out.println("Best customer (highest total sales): " + bestCustomer + " ($" + df.format(bestCustomerTotalSales) + ")");
    
        // Calculate the amount of customers per state
Map<String, Integer> customersPerState = new HashMap<>();
for (List<String[]> orders : customerData.values()) {
    for (String[] order : orders) {
        String state = order[10]; // Assuming state is at index 10
        customersPerState.put(state, customersPerState.getOrDefault(state, 0) + 1);
    }
}
System.out.println("Amount of customers per state:");
for (Map.Entry<String, Integer> entry : customersPerState.entrySet()) {
    System.out.println(entry.getKey() + ": " + entry.getValue());
}

// Count the number of Corporate, Consumer, and Home Office customers
int corporateCount = 0;
int consumerCount = 0;
int homeOfficeCount = 0;
for (List<String[]> orders : customerData.values()) {
    for (String[] order : orders) {
        String segment = order[7]; // Assuming segment is at index 7
        switch (segment) {
            case "Corporate":
                corporateCount++;
                break;
            case "Consumer":
                consumerCount++;
                break;
            case "Home Office":
                homeOfficeCount++;
                break;
            default:
                break;
        }
    }
}
System.out.println("Number of Corporate customers: " + corporateCount);
System.out.println("Number of Consumer customers: " + consumerCount);
System.out.println("Number of Home Office customers: " + homeOfficeCount);

// Calculate the total sales per year
Map<String, Double> totalSalesPerYear = new HashMap<>();
SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
for (List<String[]> orders : customerData.values()) {
    for (String[] order : orders) {
        try {
            Date orderDate = sdf.parse(order[2]); // Assuming order date is at index 2
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(orderDate);
            int year = calendar.get(Calendar.YEAR);
            String yearKey = String.valueOf(year);
            double sales = df.parse(order[16]).doubleValue(); // Assuming sales amount is at index 16
            totalSalesPerYear.put(yearKey, totalSalesPerYear.getOrDefault(yearKey, 0.0) + sales);
        } catch (ParseException e) {
            
        }
    }
}
System.out.println("Total sales per year:");
for (Map.Entry<String, Double> entry : totalSalesPerYear.entrySet()) {
    System.out.println(entry.getKey() + ": $" + df.format(entry.getValue()));
}

// Calculate the total sales per region
Map<String, Double> totalSalesPerRegion = new HashMap<>();
for (List<String[]> orders : customerData.values()) {
    for (String[] order : orders) {
        String region = order[12]; // Assuming region is at index 12
        try {
            double sales = df.parse(order[16]).doubleValue(); // Assuming sales amount is at index 16
            totalSalesPerRegion.put(region, totalSalesPerRegion.getOrDefault(region, 0.0) + sales);
        } catch (ParseException e) {
            
        }
    }
}
System.out.println("Total sales per region:");
for (Map.Entry<String, Double> entry : totalSalesPerRegion.entrySet()) {
    System.out.println(entry.getKey() + ": $" + df.format(entry.getValue()));
}
    }
    
}
