package edu.temple.cis.c3238.banksim;

import java.util.concurrent.Semaphore;

/**
 * @author Cay Horstmann
 * @author Modified by Paul Wolfgang
 *
 * @author Modified again by Philip Cappelli
 *  |?| Implemented a Semaphore to aquire/release from 10 permits so when the test
 *  |?| thread runs, a lock is in place preventing all of the accounts from making
 *  |?| transactions thus allowing for an accurate balance test result upon request.
 */

public class Bank {
    public static final int NTEST = 10;
    private final Account[] accounts;
    private TestThread testThread;
    public final Semaphore semaphore;
    private long ntransacts = 0;
    private final int initialBalance;
    private final int numAccounts;
    private boolean open;

    public Bank(int numAccounts, int initialBalance) {
        this.numAccounts = numAccounts;
        this.initialBalance = initialBalance;
        semaphore = new Semaphore(this.numAccounts);
        accounts = new Account[numAccounts];
        ntransacts = 0;
        open = true;

        for (int i = 0; i < accounts.length; i++) {
            accounts[i] = new Account(this, i, initialBalance);
        }
    }

    public void transfer(int from, int to, int amount) {
        accounts[from].waitForAvailableFunds(amount);
        if (!open) {
            return;
        }
        try {
            // Request a permit, if available...become blocked if not available.
            semaphore.acquire();
            if (accounts[from].withdraw(amount)) {
                accounts[to].deposit(amount);
            }
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            // Release the permit when transaction is complete.
            semaphore.release();
        }
        if (shouldTest()) {
            test();
        }
    }

    /**
     * Creates a new TestThread specifically to test the sum of all of the
     * accounts to determine if the total balance still totals $10,000 after
     * the many random deposit/withdrawal transactions performed by accounts.
     */
    public void test() {
        testThread = new TestThread(this, accounts, numAccounts, initialBalance);
        testThread.start();
    }

    public int size() {
        return accounts.length;
    }
    
    public synchronized boolean isOpen() {
        return open;
    }
    
    public void closeBank() {
        synchronized (this) {
            open = false;
        }
        for (Account account : accounts) {
            synchronized(account) {
                account.notifyAll();
            }
        }
    }
    
    public synchronized boolean shouldTest() {
        return ++ntransacts % NTEST == 0;
    }
}
