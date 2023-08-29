package com.br.sos.repository;

import com.br.sos.entity.TipologiaDocumento;
import com.br.sos.lib.DbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TipoDocumentoRepository {

    public List<TipologiaDocumento> findAll() throws SQLException {
        Connection connect = DbConnection.connect();
        assert connect != null;
        try (PreparedStatement statement = connect.prepareStatement("select * from tipifica.tipologia_documento where ds_palavra_chave != ''")){

            List<TipologiaDocumento> arqs = new ArrayList<>();

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                TipologiaDocumento tipologia = new TipologiaDocumento();
                tipologia.setId(resultSet.getInt(1));
                tipologia.setNuTipo(resultSet.getInt(2));
                tipologia.setNoTipo(resultSet.getString(3));
                tipologia.setDsPalavraChave(resultSet.getString(4));

                arqs.add(tipologia);
            }

            return arqs;
        }
    }

}
