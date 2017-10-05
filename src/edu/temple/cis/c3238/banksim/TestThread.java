package edu.temple.cis.c3238.banksim;

public class TestThread extends Thread {
    private final Account[] accounts;
    private final Bank bank;
    private final int numAccounts;
    private final int initialBalance;

    public TestThread(Bank bank, Account[] accounts , int accountCount, int startBalance) {
        this.bank = bank;
        this.numAccounts = accountCount;
        this.initialBalance = startBalance;
        this.accounts = accounts;
    }

    @Override
    public void run(){
        int sum = 0;
        try {
            bank.semaphore.acquire(10);
            for (Account account : accounts) {
                System.out.printf("%s %s%n", Thread.currentThread().toString(), account.toString());
                sum += account.getBalance();
            }
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            bank.semaphore.release(10);
        }

        System.out.println(Thread.currentThread().toString() + "test #" + bank.testCount++ + " Sum: " + sum);
        if (sum != numAccounts * initialBalance) {
            System.out.println(Thread.currentThread().toString() + " Money was gained or lost");
            System.exit(1);
        } else {
            System.out.println(Thread.currentThread().toString() + " The bank is in balance");
        }
    }
}
