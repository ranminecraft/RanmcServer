package cc.ranmc.server.constant;

import cc.ranmc.server.util.DataFile;
import cc.ranmc.sqlite.SQLite;

public class Sqlite {
    private final SQLite data = new SQLite(DataFile.read("sqlite"));
}
