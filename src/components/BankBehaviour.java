package components;

/**
 * Created by eva on 24/01/2017.
 */
public class BankBehaviour {
    Bank bank;

    public BankBehaviour(){
        this.bank=null;
    }

    public void printBank(){
        System.out.println(this.bank.name);
    }

    public void updateBalanceSheet(){
        this.bank.Balancesheet.phi++;
    }


}
