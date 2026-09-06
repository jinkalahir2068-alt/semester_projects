import java.util.Scanner;

public class Banksystem
{
    private String accno;
    private Double balance;
    String name;
    static int count = 0;

    Banksystem(String b_accno, Double b_balance, String b_name)
    {
        this.accno = b_accno;
        this.balance = b_balance;
        this.name = b_name;
        count++;
    }

    void checkBalance()
    {
        System.out.println("Your account details:");
        System.out.println("Account number: " + accno);
        System.out.println("Current balance: " + balance);
        System.out.println("Account holder name: " + name);
    }

    void depositMoney(double damount)
    {
        balance += damount;
        System.out.println("Deposit successful!! " + damount
                + " Your new balance: " + balance);
    }

    public void withdrawMoney(double wamount)
    {
        if (wamount <= balance)
        {
            balance -= wamount;
            System.out.println("Withdraw successful!! " + wamount
                    + " Your remaining balance: " + balance);
        }
        else
        {
            System.out.println("Transaction Failed! Insufficient balance");
        }
    }

    public static void main(String[] args)
    {
        Scanner sc = new Scanner(System.in);

       
        String username = "jinkal";
        String password = "mybank20";

        System.out.print("Enter username: ");
        String inputUsername = sc.nextLine();

        System.out.print("Enter password: ");
        String inputPassword = sc.nextLine();

        if (!inputUsername.equals(username) && !inputPassword.equals(password))
        {
            System.out.println("Invalid username or password!");
            System.out.println("Access denied. No operation can be performed.");
            sc.close();
            return;
        }

        System.out.println("Login successful!!");
        System.out.println("Welcome " + username);

        Banksystem a1 = new Banksystem("1111", 10000.0, "jinkal makwana");
        Banksystem a2 = new Banksystem("2222", 20000.0, "jaimin makwana");

        System.out.println("Total number of accounts: " + Banksystem.count);

        while (true)
        {
            System.out.println("\n1. Check Balance");
            System.out.println("2. Deposit Money");
            System.out.println("3. Withdraw Money");
            System.out.println("4. Exit from system");
            System.out.print("Enter your choice: ");

            int choice = sc.nextInt();

            switch (choice)
            {
                case 1:
                    a1.checkBalance();
                    break;

                case 2:
                    System.out.print("Enter deposit amount: ");
                    double damount = sc.nextDouble();

                    if (damount > 0)
                    {
                        a1.depositMoney(damount);
                    }
                    else
                    {
                        System.out.println("Invalid deposit amount!");
                    }
                    break;

                case 3:
                    System.out.print("Enter withdraw amount: ");
                    double wamount = sc.nextDouble();

                    if (wamount > 0)
                    {
                        a1.withdrawMoney(wamount);
                    }
                    else
                    {
                        System.out.println("Invalid withdraw amount!");
                    }
                    break;

                case 4:
                    System.out.println("Thank you for using Bank System!");
                    sc.close();
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid choice!");
            }
        }
    }
}
