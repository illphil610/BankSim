package edu.temple.cis.c3238.banksim;

/**
 * @author Philip Cappelli
 * CIS 3238 Software Design - Lab 4
 *
 * First Solution... (commented out)
 * TestThread uses the Semaphore located in Bank.java to aquire 10
 * permits (1 for each account) and to lock the critical section during
 * testing.  This benefits the bank test because all 10 accounts will not
 * be performing transactions during this time until permits are released.
 * TestThread extends Thread because in this context I didn't feel
 * implementing Runnable would make the performance any better so I just used
 * inheritence instead.
 *
 * Second Solution...
 * TestThread uses a ReentrantLock to lock the critical section to prevent
 * other threads from accessing resources during the account balance test.
 * The semaphore method account balances were not changing, but the ReentrantLock
 * was allowing for different balances after each test.
 */

public class TestThread extends Thread {
    private final Account[] accounts;
    private final Bank bank;
    private final int numAccounts;
    private final int initialBalance;

    public TestThread(Bank bank, Account[] accounts , int numAccounts, int initialBalance) {
        this.bank = bank;
        this.numAccounts = numAccounts;
        this.initialBalance = initialBalance;
        this.accounts = accounts;
    }

    @Override
    public void run() {
        int sum = 0;
        try {
            // Using the Semaphore from Bank.java to acquire 10 permits before testing
            //bank.semaphore.acquire(10);

            // Lock critical section to prevent race conditions
            bank.lock.lock();
            for (Account account : accounts) {
                System.out.printf("%s %s%n", Thread.currentThread().toString(), account.toString());
                sum += account.getBalance();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        bank.lock.unlock();
        //finally {
            // Release the permits to allow the accounts to proceed with more transactions
            // semaphore.release(10);
       // }

        System.out.println(Thread.currentThread().toString() + " Sum: " + sum);
        if (sum != numAccounts * initialBalance) {
            System.out.println(Thread.currentThread().toString() + " Money was gained or lost");
            System.exit(1);
        } else {
            System.out.println(Thread.currentThread().toString() + " The bank is in balance");
        }
    }
}
