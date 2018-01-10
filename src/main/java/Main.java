import model.AddressDataSet;
import model.PhoneDataSet;
import model.UserDataSet;
import persistence.DBService;
import persistence.impls.DBServiceHibernateImpl;
import persistence.impls.DBServiceImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by entony on 17.10.2017.
 */
public class Main {

    public static void main(String[] args) {
        dbServiceExec();
    }

    private static void dbServiceExec(){
        try(DBService dbService = new DBServiceImpl()){
        //try(DBService dbService = new DBServiceHibernateImpl()){
            AddressDataSet address = new AddressDataSet("Main");
            PhoneDataSet phone1 = new PhoneDataSet("12345");
            PhoneDataSet phone2 = new PhoneDataSet("67890");
            List<PhoneDataSet> phones = new ArrayList<>(Arrays.asList(phone1,phone2));

            UserDataSet user = new UserDataSet("Tony", 22, address, phones);
            long id = dbService.save(user);
            /*
            UserDataSet readedUser = dbService.read(UserDataSet.class, id);
            System.out.println(readedUser);

            dbService.save(new UserDataSet("Kate", 22, new AddressDataSet("Ligovsky 22"), new PhoneDataSet("772627")));

            List<UserDataSet> resultsByName = dbService.readByName(UserDataSet.class, "Kate");
            for (UserDataSet u: resultsByName){
                System.out.println(u);
            }
            List<UserDataSet> resultsAll = dbService.readAll(UserDataSet.class);
            for (UserDataSet u: resultsAll){
                System.out.println(u);
            }*/
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
