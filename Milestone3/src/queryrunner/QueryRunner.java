/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package queryrunner;


import java.util.ArrayList;
import java.util.Scanner;

/**
 * 
 * QueryRunner takes a list of Queries that are initialized in it's constructor
 * and provides functions that will call the various functions in the QueryJDBC class 
 * which will enable MYSQL queries to be executed. It also has functions to provide the
 * returned data from the Queries. Currently the eventHandlers in QueryFrame call these
 * functions in order to run the Queries.
 */
public class QueryRunner {

    
    public QueryRunner()
    {
        this.m_jdbcData = new QueryJDBC();
        m_updateAmount = 0;
        m_queryArray = new ArrayList<>();
        m_error="";
    
        
        // TODO - You will need to change the queries below to match your queries.
        
        // You will need to put your Project Application in the below variable
        
        this.m_projectTeamApplication="BooksVille";    // THIS NEEDS TO CHANGE FOR YOUR APPLICATION
        
        // Each row that is added to m_queryArray is a separate query. It does not work on Stored procedure calls.
        // The 'new' Java keyword is a way of initializing the data that will be added to QueryArray. Please do not change
        // Format for each row of m_queryArray is: (QueryText, ParamaterLabelArray[], LikeParameterArray[], IsItActionQuery, IsItParameterQuery)
        
        //    QueryText is a String that represents your query. It can be anything but Stored Procedure
        //    Parameter Label Array  (e.g. Put in null if there is no Parameters in your query, otherwise put in the Parameter Names)
        //    LikeParameter Array  is an array I regret having to add, but it is necessary to tell QueryRunner which parameter has a LIKE Clause. If you have no parameters, put in null. Otherwise put in false for parameters that don't use 'like' and true for ones that do.
        //    IsItActionQuery (e.g. Mark it true if it is, otherwise false)
        //    IsItParameterQuery (e.g.Mark it true if it is, otherwise false)
        
        m_queryArray.add(new QueryData("Select * from User", null, null, false, false));   // THIS NEEDS TO CHANGE FOR YOUR APPLICATION
        m_queryArray.add(new QueryData("SELECT g.genre_title genre, b.title book_title FROM Book b JOIN Book_Genre bg ON b.isbn = bg.isbn JOIN Genre g ON bg.genre_id = g.genre_id WHERE g.genre_title = ?", new String [] {"GENRE_TITLE"}, new boolean [] {false},  false, true));        // THIS NEEDS TO CHANGE FOR YOUR APPLICATION
        m_queryArray.add(new QueryData("Select * from contact where contact_name like ?", new String [] {"CONTACT_NAME"}, new boolean [] {true}, false, true));        // THIS NEEDS TO CHANGE FOR YOUR APPLICATION
        m_queryArray.add(new QueryData("INSERT INTO User (user_id, username, first_name, last_name, bio, zipcode) values (?,?,?,?,?,?)",new String [] {"user_id", "username", "first_name", "last_name", "bio", "zipcode"}, new boolean [] {false, false, false, false, false, false}, true, true));// THIS NEEDS TO CHANGE FOR YOUR APPLICATION
                       
    }
       

    public int GetTotalQueries()
    {
        return m_queryArray.size();
    }
    
    public int GetParameterAmtForQuery(int queryChoice)
    {
        QueryData e= m_queryArray.get(queryChoice);
        return e.GetParmAmount();
    }
              
    public String  GetParamText(int queryChoice, int parmnum )
    {
       QueryData e=m_queryArray.get(queryChoice);        
       return e.GetParamText(parmnum); 
    }   

    public String GetQueryText(int queryChoice)
    {
        QueryData e=m_queryArray.get(queryChoice);
        return e.GetQueryString();        
    }
    
    /**
     * Function will return how many rows were updated as a result
     * of the update query
     * @return Returns how many rows were updated
     */
    
    public int GetUpdateAmount()
    {
        return m_updateAmount;
    }
    
    /**
     * Function will return ALL of the Column Headers from the query
     * @return Returns array of column headers
     */
    public String [] GetQueryHeaders()
    {
        return m_jdbcData.GetHeaders();
    }
    
    /**
     * After the query has been run, all of the data has been captured into
     * a multi-dimensional string array which contains all the row's. For each
     * row it also has all the column data. It is in string format
     * @return multi-dimensional array of String data based on the resultset 
     * from the query
     */
    public String[][] GetQueryData()
    {
        return m_jdbcData.GetData();
    }

    public String GetProjectTeamApplication()
    {
        return m_projectTeamApplication;        
    }
    public boolean  isActionQuery (int queryChoice)
    {
        QueryData e=m_queryArray.get(queryChoice);
        return e.IsQueryAction();
    }
    
    public boolean isParameterQuery(int queryChoice)
    {
        QueryData e=m_queryArray.get(queryChoice);
        return e.IsQueryParm();
    }
    
     
    public boolean ExecuteQuery(int queryChoice, String [] parms)
    {
        boolean bOK = true;
        QueryData e=m_queryArray.get(queryChoice);        
        bOK = m_jdbcData.ExecuteQuery(e.GetQueryString(), parms, e.GetAllLikeParams());
        return bOK;
    }
    
     public boolean ExecuteUpdate(int queryChoice, String [] parms)
    {
        boolean bOK = true;
        QueryData e=m_queryArray.get(queryChoice);        
        bOK = m_jdbcData.ExecuteUpdate(e.GetQueryString(), parms);
        m_updateAmount = m_jdbcData.GetUpdateCount();
        return bOK;
    }   
    
      
    public boolean Connect(String szHost, String szUser, String szPass, String szDatabase)
    {

        boolean bConnect = m_jdbcData.ConnectToDatabase(szHost, szUser, szPass, szDatabase);
        if (bConnect == false)
            m_error = m_jdbcData.GetError();        
        return bConnect;
    }
    
    public boolean Disconnect()
    {
        // Disconnect the JDBCData Object
        boolean bConnect = m_jdbcData.CloseDatabase();
        if (bConnect == false)
            m_error = m_jdbcData.GetError();
        return true;
    }
    
    public String GetError()
    {
        return m_error;
    }
 
    private QueryJDBC m_jdbcData;
    private String m_error;    
    private String m_projectTeamApplication;
    private ArrayList<QueryData> m_queryArray;  
    private int m_updateAmount;
            
    /**
     * @param args the command line arguments
     */
    
    // Console App will Connect to Database
    // It will run a single select query without Parameters
    // It will display the results
    // It will close the database session
    
    public static void main(String[] args) {
        // TODO code application logic here

        final QueryRunner queryrunner = new QueryRunner();
        
        if (args.length == 0)
        {
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {

                    new QueryFrame(queryrunner).setVisible(true);
                }            
            });
        }
        else
        {
            if (args[0].equals("-console") )
            {
                
                Scanner in = new Scanner(System.in);
                System.out.println("Enter the hostname/server : ");
                String host =  in.nextLine(); // "cssql.seattleu.edu";// 
                
                System.out.println("Enter the username : ");
                String user = in.nextLine(); //"mm_sttest8b"; //
                
                System.out.println("Enter the password : ");
                String pass =  in.nextLine(); //"mm_sttest8bPass";
                
                System.out.println("Enter the database : ");
                String database = in.nextLine();// "mm_sttest8b"; // 
                
                queryrunner.m_jdbcData.ConnectToDatabase(host, user, pass, database);
                int numOFQueries = queryrunner.GetTotalQueries();
                
                boolean isQueryToBeTested = true;
                while(isQueryToBeTested){
                    System.out.println("Enter query number between ( 1 and " + numOFQueries + " )");
                    int queryChoice = in.nextInt();
                    queryChoice = queryChoice - 1;
                    System.out.println("Query : " + queryrunner.GetQueryText(queryChoice));
                    String [] parmstring = {};
                    if(queryrunner.isParameterQuery(queryChoice)){ // fetches all the parameters
                        int parameterAmt = queryrunner.GetParameterAmtForQuery(queryChoice);
                        System.out.println(parameterAmt);
                        parmstring = new String[parameterAmt];
                        for(int i = 0; i < parameterAmt; i++){
                            String label = queryrunner.m_queryArray.get(queryChoice).GetParamText(i);
                            System.out.println("Provide " + label + " : ");
                            in.nextLine();
                            parmstring[i] = in.nextLine(); // got the parameter value
                        }
                        
                        
                    }
                    
                    if(queryrunner.isActionQuery(queryChoice)){ // if it is an action query
                        
                        queryrunner.ExecuteUpdate(queryChoice, parmstring);
                        System.out.println(queryrunner.GetUpdateAmount() + " number of rows were affected");
                    }else{
                        queryrunner.ExecuteQuery(queryChoice, parmstring);
                        String[] colNames = queryrunner.GetQueryHeaders();
                        String[][] results = queryrunner.GetQueryData();
                        for(String col: colNames){
                            System.out.print(col + "               ");
                        }
                        System.out.println("\n");
                        for(int i = 0; i < results.length; i++){
                           
                            for(int j = 0; j < results[i].length; j++){
                                System.out.print(results[i][j] + "        ");
                            }
                            System.out.println("\n");
                        }
                    }
                    
                    System.out.println("Do you want to test another query? (y/n) : ");
                    // in.nextLine();
                    String testAgain = in.nextLine();
                    isQueryToBeTested = (testAgain.equals("y"))? true:false;
                    
                }
                
            }
            else
            {
               System.out.println("usage: you must use -console as your argument to get non-gui functionality. If you leave it out it will be GUI");
            }
        }

    }    
}
