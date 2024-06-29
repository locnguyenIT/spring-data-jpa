package com.devcoding.springdatajpa;

import com.devcoding.springdatajpa.customer.Customer;
import com.devcoding.springdatajpa.customer.CustomerRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.event.internal.EntityState;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@Slf4j
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(CustomerRepository customerRepository, EntityManagerFactory entityManagerFactory) {
		return args -> {

			EntityManager entityManager = entityManagerFactory.createEntityManager();

			SessionImplementor sessionImplementor = entityManager.unwrap(SessionImplementor.class);


			entityManager.getTransaction().begin();

			Customer jason = new Customer("jason","jason@gmail.com","US");
//            jason.setEmail("jason2@gmail.com");
			checkEntityState(sessionImplementor, jason);

			log.info("Before Persist");
			entityManager.persist(jason);
			checkEntityState(sessionImplementor, jason);

			log.info("Before detach");
			entityManager.detach(jason);
			checkEntityState(sessionImplementor, jason);

			log.info("Before merge");
			jason = entityManager.merge(jason);
			checkEntityState(sessionImplementor, jason);

			log.info("Before Remove");
			entityManager.remove(jason);
			checkEntityState(sessionImplementor, jason);

			log.info("Before re-persist after Remove");
			entityManager.persist(jason);
			checkEntityState(sessionImplementor, jason);
//			boolean contains = entityManager.contains(jason);
//			if (contains) {
//				System.out.println("Entity is in Persistent state");
//			}
			entityManager.getTransaction().commit();
			entityManager.clear();
//			boolean isDetached = entityManager.contains(jason);
//			if (!isDetached) {
//				System.out.println("Entity is in Detached state");
//			}

		};
	}

	private static void checkEntityState(SessionImplementor sessionImplementor, Customer customer) {
		EntityEntry entityEntry = sessionImplementor.getPersistenceContext().getEntry(customer);
		EntityPersister persisted = sessionImplementor.getEntityPersister(Customer.class.getName(), customer);
		String entityName = persisted.getEntityName();
		EntityState state = EntityState.getEntityState(
				customer,
				entityName,
				entityEntry,
				sessionImplementor,
				null
		);
		log.info("Entity state is {}", state);
	}

}
