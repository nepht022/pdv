package net.originmobi.pdv.repository;

<<<<<<< HEAD
import java.util.List;
=======
>>>>>>> 8ed96d5d727adec0606a912a0b4a2c65bc0d54fd
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
<<<<<<< HEAD
import org.springframework.stereotype.Repository;
=======
>>>>>>> 8ed96d5d727adec0606a912a0b4a2c65bc0d54fd
import org.springframework.transaction.annotation.Transactional;

import net.originmobi.pdv.model.Empresa;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

	@Query(value = "select * from empresa", nativeQuery = true)
	Optional<Empresa> buscaEmpresaCadastrada();
<<<<<<< HEAD
	
=======

>>>>>>> 8ed96d5d727adec0606a912a0b4a2c65bc0d54fd
	@Transactional
	@Modifying
	@Query(value = "update empresa set nome = :nome, nome_fantasia = :nome_fantasia, cnpj = :cnpj, ie = :ie, regime_tributario_codigo = :regime "
			+ "where codigo = :codigo", nativeQuery = true)
	void update(@Param("codigo") Long codigo, @Param("nome") String nome, @Param("nome_fantasia") String nome_fantasia,
			@Param("cnpj") String cnpj, @Param("ie") String ie, @Param("regime") Long codRegime);

}
