create table sequences (sequence_id varchar PRIMARY KEY, current_value numeric);

-- automated tes input datasets (for different tests)
insert into sequences values ('A32', 0);
insert into sequences values ('B40', 20);

insert into sequences values ('Y99', 0);
insert into sequences values ('Z87', 2);

-- stress test
create table generated_sequences(sequence_id varchar, current_value numeric);

CREATE UNIQUE INDEX generated_idx
ON generated_sequences (sequence_id, current_value);
