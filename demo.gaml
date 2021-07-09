model modelname

species abc {

}

global {
	bool x <- 1;
	int asd <- 1;
	list<abc> l1 <- [];
	list<nonexisttype> l1 <- [];
	list<float> l2 <- [];
	list<abc> l3 <- [];
	float energy_miss <- 0.025; //nang luong mat khi gap hoa
	float prob_move <- 0.99;
	int nb_bphs_init <- 20; // so ray khoi tao
	float bph_max_energy <- 1.0;
	float bph_max_transfer <- 0.1;
	float bph_energy_consum <- 0.025;
	float egg_proba_die <- 0.0025;
	float bph_proba_die <- 0.04;
	float bph_proba_reproduce <- 0.07;
	int age_init <- 168; // tuoi de co the di chuyen
	float age_reproduce <- 504;
	float bph_energy_reproduce <- 0.8;
	int bph_nb_max_offsprings <- 12;
	int bph_nb_min_offsprings <- 5;
	float bph_energy_move <- 0.2; //nang luong thap nhat de di chuyen
	string file_name <- "result1.csv";
	int nb_bph1j -> length(bph where ((each.age < age_init) and (each.my_cell.grid_x < flower_x_start)));
	int nb_bph2j -> length(bph where ((each.age < age_init) and (each.my_cell.grid_x >= flower_x_end)));
	int nb_bph1a -> length(bph where (each.age >= age_init and each.my_cell.grid_x < flower_x_start));
	int nb_bph2a -> length(bph where (each.age >= age_init and each.my_cell.grid_x >= flower_x_end));
	int nb_paddy1 -> length(vegetation_cell where (each.food > 0 and each.grid_x < flower_x_start));
	int nb_paddy2 -> length(vegetation_cell where (each.food > 0 and each.grid_x >= flower_x_end));
	int field_width <- 100;
	int field_height <- 100;
	int flower_width <- 16;
	int flower_x_start -> int(floor(field_width / 2 - flower_width / 2));
	int flower_x_end -> int(floor(field_width / 2 + flower_width / 2));
	string bphs_initial_spawn_position <- "nn";
	int total_bph <- 0;
	int total_passed <- 0;

	init {
		create bph number: nb_bphs_init {
		}

		ask vegetation_cell {
			if self.grid_x < flower_x_start or self.grid_x >= flower_x_end {
				food <- 0.4;
				color <- rgb(int(255 * (1 - food)), 255, int(255 * (1 - food)));
			} else {
				self.color <- rgb(255, 255, 0);
				self.food <- 0;
			}

			food_prod <- 0.005;
		}

	}

	reflex aff when: mod(cycle, 500) = 0 {
		write "message at #" + cycle + "/2880";
	}

	reflex stop_simulation when: (cycle = 2880) {
		do pause;
	}

}

species bph {
	float size <- 1.0;
	rgb color;
	int bphs_number_reproduce <- 0;
	float max_energy <- bph_max_energy;
	float max_transfer <- bph_max_transfer;
	float energy_consum <- bph_energy_consum;
	float proba_reproduce <- bph_proba_reproduce;
	int nb_max_offsprings <- bph_nb_max_offsprings;
	float energy_reproduce <- bph_energy_reproduce;
	float energy_move <- bph_energy_move;
	vegetation_cell my_cell;
	float energy <- rnd(0.4, 0.6) max: max_energy;
	int age <- rnd(0, 299) update: age + 1;
	bool counted_as_passed <- false;

	init {
		if bphs_initial_spawn_position = "nn" {
			my_cell <- one_of(vegetation_cell where (each.grid_x < flower_x_start));
		}

		if bphs_initial_spawn_position = "goc" {
			my_cell <- one_of(vegetation_cell where (each.grid_x < 5 and each.grid_y < 5));
		}

		if bphs_initial_spawn_position = "bien" {
			my_cell <- one_of(vegetation_cell where (each.grid_x < 2));
		}

		total_bph <- total_bph + 1;
		location <- my_cell.location;
	}

	reflex increase_counter when: not counted_as_passed and location.x > flower_x_end {
		counted_as_passed <- true;
		total_passed <- total_passed + 1;
	}

	reflex update_energy when: age >= age_init {
		energy <- energy - energy_consum;
	}

	reflex miss_nl when: my_cell.grid_x >= flower_x_start and my_cell.grid_x < flower_x_end {
		energy <- energy - energy_miss;
	}

	reflex basic_move when: energy >= energy_move and age >= age_init and flip(prob_move) {
		my_cell <- one_of(my_cell.neighbors2);
		location <- my_cell.location;
	}

	reflex change_color {
		color <- (age < age_init) ? #blue : #red;
	}

	reflex eat when: my_cell.food > 0 and age >= age_init {
		float energy_transfer <- min([max_transfer, my_cell.food]);
		my_cell.food <- my_cell.food - energy_transfer;
		energy <- energy + energy_transfer;
	}

	reflex die when: energy <= 0 or age > 720 or (flip(bph_proba_die) and age >= 600 and age <= 720) or (age < age_init and flip(egg_proba_die)) {
		do die;
	}

	reflex reproduce when: (energy >= energy_reproduce) and (age >= age_reproduce) and (bphs_number_reproduce < 21) and (flip(proba_reproduce)) {
		bphs_number_reproduce <- bphs_number_reproduce + 1;
		int nb_offsprings <- rnd(1, nb_max_offsprings);
		create species(self) number: nb_offsprings {
			my_cell <- myself.my_cell;
			location <- my_cell.location;
			energy <- 0.4;
			age <- 0;
		}

		energy <- energy - 0.1;
	}

	aspect base {
		draw circle(size) color: color;
	}

}

grid vegetation_cell width: field_width height: field_height neighbors: 4 {
	float max_food <- 1.0;
	float food_prod;
	float food max: max_food;
	list<vegetation_cell> neighbors2 <- (self neighbors_at 2);

	reflex update_color {
		if self.grid_x < flower_x_start or self.grid_x >= flower_x_end {
			color <- rgb(int(255 * (1 - food)), 255, int(255 * (1 - food)));
		}
	}

	reflex update_food when: food > 0 {
		food <- food + food * 0.008;
	}
}

experiment counting type: gui until: (length(bph) = 0 and cycle > 1) or cycle = 2880 {
	parameter nb_bphs_init var: nb_bphs_init;
	parameter flower_width var: flower_width;
	parameter bphs_initial_spawn_position var: bphs_initial_spawn_position among: ["nn", "goc", "bien"];

	reflex WriteProb when: length(bph) = 0 and cycle > 1 or cycle = 2880 {
		string filename <- "e_" + nb_bphs_init + "_" + flower_width + "_" + bphs_initial_spawn_position + ".txt";
		string data <- "" + total_passed + "/" + total_bph;
		save data: data type: "text" to: filename rewrite: false;
	}
}
