package components;

/**
 * Created by eva on 24/01/2017.
 */
public class Bank {
    String name;
    Boolean D;
    BankBalanceSheet Balancesheet;
    BankBehaviour Behaviour;

    public Bank(String name){
        this.name=name;
        this.D=true;
        this.Behaviour=new BankBehaviour();
        this.Behaviour.bank=this;
    }

    public void setBalancesheet(BankBalanceSheet Balancesheet){
        this.Balancesheet=Balancesheet;
        Balancesheet.bank=this;
    }

    public BankBalanceSheet getBalancesheet(){
        return this.Balancesheet;
    }

    public void printName(){
        System.out.println(this.name);
    }

    public void printStockValue(){
        System.out.println(this.Balancesheet.phi);
    }

    public BankBehaviour getBehaviour(){
        return this.Behaviour;
    }
}
