--
-- TOC entry 210 (class 1259 OID 16556)
-- Name: kanji; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE "kanji" (
    "id" bigint NOT NULL,
    "time_stamp" timestamp without time zone,
    "value" character varying(255)
);

--
-- TOC entry 211 (class 1259 OID 16561)
-- Name: kanji_kun_yomi; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE "kanji_kun_yomi" (
    "kanji_id" bigint NOT NULL,
    "kun_yomi" character varying(255)
);


--
-- TOC entry 212 (class 1259 OID 16569)
-- Name: kanji_on_yomi; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE "kanji_on_yomi" (
    "kanji_id" bigint NOT NULL,
    "on_yomi" character varying(255)
);


--
-- TOC entry 213 (class 1259 OID 16577)
-- Name: kanji_translation; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE "kanji_translation" (
    "kanji_id" bigint NOT NULL,
    "translation" character varying(255)
);


--
-- TOC entry 215 (class 1259 OID 16586)
-- Name: word; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE "word" (
    "id" bigint NOT NULL,
    "furigana_value" character varying(255),
    "time_stamp" timestamp without time zone,
    "translation" character varying(255),
    "value" character varying(255)
);


--
-- TOC entry 216 (class 1259 OID 16593)
-- Name: word_kanji; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE "word_kanji" (
    "word_id" bigint NOT NULL,
    "kanji_id" bigint NOT NULL
);


--
-- TOC entry 3186 (class 2606 OID 16560)
-- Name: kanji kanji_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE "kanji"
    ADD CONSTRAINT "kanji_pkey" PRIMARY KEY ("id");


--
-- TOC entry 3188 (class 2606 OID 16592)
-- Name: word word_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE "word"
    ADD CONSTRAINT "word_pkey" PRIMARY KEY ("id");


--
-- TOC entry 3191 (class 2606 OID 16580)
-- Name: kanji_translation fk588vmti9fq13gj8pd4cgt5e4d; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE "kanji_translation"
    ADD CONSTRAINT "fk588vmti9fq13gj8pd4cgt5e4d" FOREIGN KEY ("kanji_id") REFERENCES "kanji"("id");


--
-- TOC entry 3192 (class 2606 OID 16596)
-- Name: word_kanji fk5l2xit9ourucp8n17oas3yh3i; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE "word_kanji"
    ADD CONSTRAINT "fk5l2xit9ourucp8n17oas3yh3i" FOREIGN KEY ("kanji_id") REFERENCES "kanji"("id");


--
-- TOC entry 3193 (class 2606 OID 16601)
-- Name: word_kanji fk9k0ibk7nwps4iwreqe2b5ki61; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE "word_kanji"
    ADD CONSTRAINT "fk9k0ibk7nwps4iwreqe2b5ki61" FOREIGN KEY ("word_id") REFERENCES "word"("id");


--
-- TOC entry 3190 (class 2606 OID 16572)
-- Name: kanji_on_yomi fkj3rl38wyq6bb0vivtpsra2694; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE "kanji_on_yomi"
    ADD CONSTRAINT "fkj3rl38wyq6bb0vivtpsra2694" FOREIGN KEY ("kanji_id") REFERENCES "kanji"("id");


--
-- TOC entry 3189 (class 2606 OID 16564)
-- Name: kanji_kun_yomi fklff2p4x1taqtuicrhfc5fds0i; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE "kanji_kun_yomi"
    ADD CONSTRAINT "fklff2p4x1taqtuicrhfc5fds0i" FOREIGN KEY ("kanji_id") REFERENCES "kanji"("id");

