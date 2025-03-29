package com.example.psicowise_backend_spring.repository.common;

import com.example.psicowise_backend_spring.entity.common.Telefone;
import com.example.psicowise_backend_spring.enums.common.TipoProprietario;
import com.example.psicowise_backend_spring.enums.common.TipoTelefone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TelefoneRepository extends JpaRepository<Telefone, UUID> {

    List<Telefone> findByProprietarioId(UUID proprietarioId);

    List<Telefone> findByTipoProprietario(TipoProprietario tipoProprietario);

    List<Telefone> findByProprietarioIdAndTipoProprietario(UUID proprietarioId, TipoProprietario tipoProprietario);

    List<Telefone> findByProprietarioIdAndTipoProprietarioAndTipo(
            UUID proprietarioId, TipoProprietario tipoProprietario, TipoTelefone tipo);

    Optional<Telefone> findByProprietarioIdAndTipoProprietarioAndPrincipal(
            UUID proprietarioId, TipoProprietario tipoProprietario, boolean principal);

    List<Telefone> findByWhatsapp(boolean whatsapp);

    List<Telefone> findByProprietarioIdAndTipoProprietarioAndWhatsapp(
            UUID proprietarioId, TipoProprietario tipoProprietario, boolean whatsapp);

    @Query("SELECT t FROM Telefone t WHERE t.proprietarioId = :proprietarioId AND t.tipoProprietario = :tipoProprietario AND t.whatsapp = true ORDER BY t.principal DESC")
    List<Telefone> findWhatsappByProprietario(
            @Param("proprietarioId") UUID proprietarioId,
            @Param("tipoProprietario") TipoProprietario tipoProprietario);

    /**
     * Encontra telefones que precisam ser migrados (não possuem proprietarioId ou tipoProprietario)
     */
    @Query("SELECT t FROM Telefone t WHERE t.proprietarioId IS NULL OR t.tipoProprietario IS NULL")
    List<Telefone> findByProprietarioIdIsNullOrTipoProprietarioIsNull();
}