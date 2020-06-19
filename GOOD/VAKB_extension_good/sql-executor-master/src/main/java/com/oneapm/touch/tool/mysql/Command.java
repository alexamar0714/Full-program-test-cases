package com.oneapm.touch.tool.mysql;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.apache.ibatis.jdbc.ScriptRunner;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;

public class Command {

    @Parameter(names = {"-h", "--help", "-?"}, description = "print the help message", help = true)
    private boolean help;

    @Parameter(names = "--host", description = "Database Host", required = true)
    private String host;

    @Parameter(names = "--port", description = "Database Port", required = true)
    private Integer port;

    @Parameter(names = "--user", description = "Database User", required = true)
    private String username;

    @Parameter(names = "--password", description = "Database Password", required = true)
    private String password;

    @Parameter(names = "--database", description = "Database Name", required = true)
    private String database;

    @Parameter(names = "--file", description = "The SQL file to execute", required = true)
    private String file;

    public static void main(String[] args) throws IOException, SQLException {
        Command command = new Command();
        try {
            final JCommander commander = new JCommander(command, args);
            if (command.help) {
                commander.usage();
                System.exit(0);
            }
        } catch (Exception e) { // NOSONAR
            System.out.println(e);
            System.exit(0);
        }
        command.run();
    }

    private void run() throws IOException, SQLException {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        dataSource.setUser(username);
        dataSource.setPassword(password);

        try (FileInputStream inputStream = new FileInputStream(file)) {
            ScriptRunner runner = new ScriptRunner(dataSource.getConnection());
            runner.setAutoCommit(true);
            runner.setStopOnError(true);
            runner.runScript(new InputStreamReader(inputStream));
            runner.closeConnection();
        }
    }
}
