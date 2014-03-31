package wallace.tom.restingActor.service

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess

import grails.plugins.rest.client.RestBuilder
import org.codehaus.groovy.grails.web.json.JSONObject
import org.codehaus.groovy.grails.web.servlet.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.web.client.MockRestServiceServer

class ImdbRequestServiceSpecification extends spock.lang.Specification {

    ImdbRequestService imdbRequestService = new ImdbRequestService()

    def "Ensure actorName is valid - throws IllegalArgumentException"() {
        when:

        imdbRequestService.validateActorName(actorName)

        then:

        thrown(IllegalArgumentException)

        where:

        actorName << [null, '']
    }

    def "Test that request goes to correct address, involves a GET, and includes the User-Agent header"() {
        given:

        def actorName = 'MyName'
        def builder = new RestBuilder()
        final mockServer = MockRestServiceServer.createServer(builder.restTemplate)
        mockServer.expect(requestTo("http://www.imdb.com/xml/find?json=1&nr=1&nm=on&q=$actorName"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.ACCEPT, "application/json"))
                .andExpect(header(HttpHeaders.USER_AGENT, 'Mozilla/5.0 Ubuntu/8.10 Firefox/27.0.1'))
                .andRespond(withSuccess('{"name":"results"}', MediaType.APPLICATION_JSON))

        when:

        def result = imdbRequestService.collectQueryResponse('MyName', builder)

        then:

        mockServer.verify()
        result != null
        result instanceof JSONObject
    }

    def "Test building the query URL with actorName"() {
        when:

        def result = imdbRequestService.buildQueryUrl(actorName)

        then:

        result == expected

        where:

        actorName | expected
        'Tom'             | 'http://www.imdb.com/xml/find?json=1&nr=1&nm=on&q=Tom'
        'Dave'            | 'http://www.imdb.com/xml/find?json=1&nr=1&nm=on&q=Dave'
        'Jennifer Garner' | 'http://www.imdb.com/xml/find?json=1&nr=1&nm=on&q=Jennifer+Garner'

    }

    def "Test limitJsonToActors with actors"() {
        given:

        String input = '{"name_approx":[{"id":"nm0908890","title":"","description":"Director, It","name":"Tommy Lee Wallace"},{"id":"nm0908887","title":"","description":"Camera and Electrical Department, X2","name":"Tom A. Wallace"},{"id":"nm3754787","title":"","description":"Actor, Sidewalk Stories","name":"Tom Wallace"},{"id":"nm4319580","title":"","description":"Editor, Confidence Game","name":"Tom Wallace"},{"id":"nm6340376","title":"","description":"Actor, Winnerman","name":"Tom Wallace"},{"id":"nm2739184","title":"","description":"Actor, A Field of Honor","name":"Tom Wallace"},{"id":"nm0908886","title":"","description":"Actor, The Girl from Porcupine","name":"Tom Wallace"},{"id":"nm2207039","title":"","description":"Visual Effects, The Chronicles of Narnia: The Lion, the Witch and the Wardrobe","name":"Timothy Wallace"},{"id":"nm0908882","title":"","description":"Camera and Electrical Department, Bolero","name":"Tim Wallace"},{"id":"nm4890126","title":"","description":"Producer, Autumn of Route 66","name":"Tim Wallace"},{"id":"nm4184451","title":"","description":"Stunts, The ABCs of Death","name":"Timothy Wallace"},{"id":"nm1281637","title":"","description":"Miscellaneous Crew, The Creature Wasn&#x27;t Nice","name":"Tim Wallace"},{"id":"nm4651871","title":"","description":"Costume and Wardrobe Department, 29.08.1942","name":"Tim Wallace"},{"id":"nm2334357","title":"","description":"Self, Bloodline","name":"Timothy Wallace-Murphy"},{"id":"nm0908881","title":"","description":"Actor, Nomads","name":"Tim Wallace"},{"id":"nm1022546","title":"","description":"Actor, Kick or Die","name":"Tim Wallace"},{"id":"nm1240740","title":"","description":"Stunts, River of No Return","name":"Tim Wallace"},{"id":"nm2644082","title":"","description":"Animation Department, Officer Buckle and Gloria","name":"Robert M. Wallace"},{"id":"nm0908838","title":"","description":"Director, Strangebrew","name":"Robert Milton Wallace"},{"id":"nm1316167","title":"","description":"Producer, Violent Blue","name":"Clinton H. Wallace"}]}'


        when:

        String result = imdbRequestService.limitJsonToActors(input)

        then:

        result.contains('Actor,')
        !result.contains('Director,')
        !result.contains('Camera and Electrical Department,')
        !result.contains('Editor,')
    }

    def "Test limitJsonToActors with actresses"() {
        given:

        String input = '{"name_approx":[{"id":"nm4216726","title":"","description":"Actress, Fright Flick","name":"Jennifer Garner"},{"id":"nm3871932","title":"","description":"Second Unit Director or Assistant Director, Down, Right, Hearted.","name":"Jennifer Garner"},{"id":"nm1329703","title":"","description":"Uncategorised","name":"Jennifer Garner"},{"id":"nm0306972","title":"","description":"Miscellaneous Crew, Sleepless in Seattle","name":"Jennifer Gardner"},{"id":"nm1751958","title":"","description":"Writer, Peace Love &#x26; Beats","name":"Jennifer Warner"},{"id":"nm4160709","title":"","description":"Costume Designer, Doorways","name":"Jennifer Garnet Filo"},{"id":"nm0306971","title":"","description":"Actress, Passion Fish","name":"Jennifer Gardner"},{"id":"nm1783529","title":"","description":"Actress, Kohan II: Kings of War","name":"Jennifer Gardner"},{"id":"nm2556819","title":"","description":"Actress, Az&#xFA;car amarga","name":"Jennifer Gardner"},{"id":"nm1927878","title":"","description":"Production Manager, Invader","name":"Jennifer Gardner"},{"id":"nm3144518","title":"","description":"Self, THS Investigates: Hot for Student","name":"Jennifer Varner"},{"id":"nm4312768","title":"","description":"Production Manager, The Missing Link","name":"Jennifer Warner"},{"id":"nm2655928","title":"","description":"Producer, Death Cheaters","name":"Jennifer Warner"},{"id":"nm2066112","title":"","description":"Self, Episode dated 22 August 2013","name":"Jennifer Warner"},{"id":"nm2257672","title":"","description":"Miscellaneous Crew, Walk Away and I Stumble","name":"Jennifer Warner"},{"id":"nm3737971","title":"","description":"Miscellaneous Crew, Episode #1.2","name":"Jennifer Warner"},{"id":"nm1752860","title":"","description":"Actress, Cold Readings","name":"Jennifer Gardner"},{"id":"nm5905299","title":"","description":"Actress, Ici","name":"Jennifer Garnet"},{"id":"nm5106829","title":"","description":"Actress, The Mid-Life Crisis Guide to Strippers","name":"Jennifer Gartner"}],"name_popular":[{"id":"nm0004950","title":"","description":"Actress, Juno","name":"Jennifer Garner"}]}'


        when:

        String result = imdbRequestService.limitJsonToActors(input)
        println(result)
        then:

        result.contains('Actress,')
        !result.contains('Producer,')
        !result.contains('Miscellaneous Crew,')
    }

    def "Test limitJsonToActors no actors or actresses"() {
        given:

        String input = '{"name_approx":[{"id":"nm3871932","title":"","description":"Second Unit Director or Assistant Director, Down, Right, Hearted.","name":"Jennifer Garner"},{"id":"nm1329703","title":"","description":"Uncategorised","name":"Jennifer Garner"},{"id":"nm0306972","title":"","description":"Miscellaneous Crew, Sleepless in Seattle","name":"Jennifer Gardner"},{"id":"nm1751958","title":"","description":"Writer, Peace Love &#x26; Beats","name":"Jennifer Warner"},{"id":"nm4160709","title":"","description":"Costume Designer, Doorways","name":"Jennifer Garnet Filo"}]}'


        when:

        String result = imdbRequestService.limitJsonToActors(input)

        then:

        result == '{"name_approx":[]}'
    }

    def "Test limitJsonToActors empty coming in"() {
        given:

        String input = '{}'

        when:

        String result = imdbRequestService.limitJsonToActors(input)

        then:

        result == '{}'
    }

    def "Test limitJsonToActors name_exact"() {
        given:

        String input = '{"name_exact":[{"id":"nm3719927","title":"","description":"Actor, Here Comes the Bride","name":"Tom Rodriguez"},{"id":"nm0005048","title":"","description":"Actor, The Mist","name":"Thomas Jane"},{"id":"nm0423388","title":"","description":"Soundtrack, V for Vendetta","name":"Antonio Carlos Jobim"},{"id":"nm1059755","title":"","description":"Animation Department, The SpongeBob SquarePants Movie","name":"Tom Yasumi"},{"id":"nm0740951","title":"","description":"Stunts, Pirates of the Caribbean: The Curse of the Black Pearl","name":"Thomas Rosales Jr."},{"id":"nm0457511","title":"","description":"Stunts, Godzilla: Final Wars","name":"Tsutomu Kitagawa"},{"id":"nm0001855","title":"","description":"Actor, Back to the Future","name":"Thomas F. Wilson"},{"id":"nm0506201","title":"","description":"Writer, Studio One in Hollywood","name":"Alfred Lewis Levitt"},{"id":"nm1672861","title":"","description":"Make-Up Department, Daredevil","name":"Tom Devlin"},{"id":"nm0370809","title":"","description":"Music Department, Basic Instinct","name":"Tom Hayden"},{"id":"nm1373700","title":"","description":"Writer, La audiencia ten&#xED;a un precio","name":"Tom Roca"},{"id":"nm1798644","title":"","description":"Self, Survivor: Palau - The Reunion","name":"Tom Westman"},{"id":"nm0495281","title":"","description":"Actor, Dumb &#x26; Dumber","name":"Tom Leasca"},{"id":"nm0866284","title":"","description":"Actor, There&#x27;s Something About Mary","name":"Tom"},{"id":"nm0225725","title":"","description":"Miscellaneous Crew, Back to the Future Part III","name":"Thomas S. Dickson"},{"id":"nm0230884","title":"","description":"Soundtrack, Mission: Impossible III","name":"Thomas Dolby"},{"id":"nm0332053","title":"","description":"Camera and Electrical Department, The Bourne Ultimatum","name":"Thomas Gottschalk"},{"id":"nm0997990","title":"","description":"Actor, The Land of College Prophets","name":"Thomas Edward Seymour"},{"id":"nm4993817","title":"","description":"Self, Roy Requejo/Tom Doromal/Alec Dungo/Ryan Boyce/Bea Alonzo/John Lloyd Cruz","name":"Tom Doromal"},{"id":"nm2160167","title":"","description":"Art Department, Copycat","name":"Thomas Hazlett"},{"id":"nm0540519","title":"","description":"Music Department, Saturday Night Live","name":"Tom Malone"},{"id":"nm0253672","title":"","description":"Writer, Cats","name":"T.S. Eliot"},{"id":"nm1468283","title":"","description":"Actor, Let the Right One In","name":"Tom Ljungman"},{"id":"nm1122778","title":"","description":"Camera and Electrical Department, Death to Smoochy","name":"Toomas Loo"},{"id":"nm0755603","title":"","description":"Actor, Loser","name":"Thomas Sadoski"},{"id":"nm0217691","title":"","description":"Soundtrack, American Pie","name":"Thomas DeLonge"},{"id":"nm2297382","title":"","description":"Actor, Maid","name":"Thienchai Jayasvasti Jr."},{"id":"nm0565339","title":"","description":"Actor, Mannequin","name":"Thomas J. McCarthy"},{"id":"nm5430504","title":"","description":"Actor, Atsuki kokoro wo","name":"Tom"},{"id":"nm1844613","title":"","description":"Actor, The Brice Man","name":"Tom"},{"id":"nm0021105","title":"","description":"Actor, Mrs Henderson Presents","name":"Thomas Allen"},{"id":"nm6135054","title":"","description":"Self, Episode #8.1","name":"Tom Alston"},{"id":"nm0263269","title":"","description":"Soundtrack, Casino","name":"Tom Evans"},{"id":"nm3290044","title":"","description":"Self, Episode #1.8","name":"Thomas Lever"},{"id":"nm1577973","title":"","description":"Self, There&#x27;s Something About Miriam","name":"Tom"},{"id":"nm4565428","title":"","description":"Self, Episode #3.6","name":"Tom"},{"id":"nm6334410","title":"","description":"Self, Episode #18.8","name":"Tom Ferrell"},{"id":"nm3179010","title":"","description":"Visual Effects, Resident Evil 6","name":"Tomonori Hirata"},{"id":"nm2869301","title":"","description":"Writer, Episode #1.1","name":"Thomas O&#x27;Donnell"},{"id":"nm5004222","title":"","description":"Self, Episode #8.1","name":"Tom Pearce"},{"id":"nm4831993","title":"","description":"Actor, Asdfmovie2","name":"Thomas Ridgewell"},{"id":"nm1949432","title":"","description":"Transportation Department, The Celestine Prophecy","name":"Tom"},{"id":"nm2483535","title":"","description":"Actor, Particularly Now, in Spring","name":"Tom"},{"id":"nm1153036","title":"","description":"Actor, Green Eyes","name":"Thomas Wesson"},{"id":"nm3745533","title":"","description":"Self, Reality Bytes","name":"Thomas D. Akers"},{"id":"nm1163060","title":"","description":"Art Department, Sugar &#x26; Spice","name":"Thomas R. Anderson"},{"id":"nm0358941","title":"","description":"Actor, Macbeth","name":"Thomas Hampson"},{"id":"nm1912642","title":"","description":"Actor, Til Death","name":"Royce Johnson"},{"id":"nm2482847","title":"","description":"Actor, Mulligans","name":"Thomas Orr-Loney"},{"id":"nm2473715","title":"","description":"Self, Can Horses Smell Fear?","name":"Tom Rock"},{"id":"nm3018646","title":"","description":"Actor, Behind Your Eyes","name":"Tom Sandoval"},{"id":"nm2042741","title":"","description":"Sound Department, The Indian","name":"Thomas A. Smith"},{"id":"nm6100966","title":"","description":"Self, Hey Arnold: The Movie - Behind the Scenes!","name":"Tom"},{"id":"nm4873534","title":"","description":"Self, Staining a Cedar Fence/Installing Window Boxes","name":"Tom"},{"id":"nm1850834","title":"","description":"Miscellaneous Crew, The Island","name":"Thomas Zellen"},{"id":"nm0009912","title":"","description":"Editor, A Voice from Heaven","name":"Thomas Acito"},{"id":"nm2694225","title":"","description":"Camera and Electrical Department, Wesley","name":"Tommy Flaherty"},{"id":"nm4889127","title":"","description":"Self, Episode #7.10","name":"Tom Kilbey"},{"id":"nm4963953","title":"","description":"Visual Effects, The Employer","name":"Tomas Simonsen"},{"id":"nm1154313","title":"","description":"Self, Color Creations by The Color Bunch","name":"Tom"},{"id":"nm5807764","title":"","description":"Art Department, Cadillacs and Dinosaurs","name":"Tom"},{"id":"nm6072497","title":"","description":"Camera and Electrical Department, Zombie 108","name":"Tom"},{"id":"nm2089139","title":"","description":"Actor, Perfect Day","name":"Tom"},{"id":"nm4864146","title":"","description":"Self, Episode #7.84","name":"Tom"},{"id":"nm3230635","title":"","description":"Miscellaneous Crew, Like Dandelion Dust","name":"Thomas H. Vidal"},{"id":"nm3737689","title":"","description":"Actor, We Bought a Zoo","name":"Thomas R. Baker"},{"id":"nm0219391","title":"","description":"Self, Misuse of Power","name":"Alfred Denning"},{"id":"nm2023302","title":"","description":"Actor, Med sina bara h&#xE4;nder","name":"Tomas Glaving"},{"id":"nm3126518","title":"","description":"Producer, Wobble: The Weight of the Truth","name":"Thomas P. Keenan"},{"id":"nm1452142","title":"","description":"Self, Episode #3.1","name":"Tom Olaerts"},{"id":"nm5829699","title":"","description":"Self, 2 Chefs Compete","name":"Tom Poehnelt"},{"id":"nm2302036","title":"","description":"Sound Department, TumblePop","name":"Tom Sato"},{"id":"nm2138064","title":"","description":"Self, Playboy: Farrah Fawcett, All of Me","name":"Tom Staebler"},{"id":"nm5591264","title":"","description":"Music Department, Inside/Outside the Beltway","name":"Tom"},{"id":"nm5816090","title":"","description":"Stunts, Crime Patrol 2: Drug Wars","name":"Tom"},{"id":"nm4241110","title":"","description":"Production Manager, Lie pao xing dong","name":"Tom"},{"id":"nm4474141","title":"","description":"Self, Episode #3.5","name":"Tom"},{"id":"nm4762882","title":"","description":"Actor, Kenneyville","name":"Tom"},{"id":"nm2381077","title":"","description":"Actor, Saving Grace","name":"Tom"},{"id":"nm3339329","title":"","description":"Actor, Sarah... ang munting prinsesa","name":"Tom"},{"id":"nm3698299","title":"","description":"Actor, Permanent Residence","name":"Tom"},{"id":"nm3906459","title":"","description":"Self, Episode dated 4 May 2010","name":"Tom"},{"id":"nm6334420","title":"","description":"Self, Episode #18.8","name":"Tom Ward"},{"id":"nm3052853","title":"","description":"Editor, Vegan Fly Trap","name":"Thomas Nolle"},{"id":"nm3300759","title":"","description":"Actor, Contact","name":"Thomas Reid"},{"id":"nm5759537","title":"","description":"Self, Women of Afghanistan/Married Priests/Boys","name":"Tom"},{"id":"nm3135955","title":"","description":"Self, Tying the Knot","name":"Tom"},{"id":"nm3876954","title":"","description":"Self, Episode #1.9","name":"Tom"},{"id":"nm3641160","title":"","description":"Self, Some Like it Hot","name":"Tom Vitale"},{"id":"nm2837726","title":"","description":"Actor, No Beers for Bradley","name":"Thomas Wallace"},{"id":"nm2151760","title":"","description":"Self, BJ and Racquia","name":"Thomas Bridegroom"},{"id":"nm4135131","title":"","description":"Actor, Fool&#x27;s Fire","name":"Thomas Patrick"},{"id":"nm1430805","title":"","description":"Self, Paradise Hotel","name":"Tom Rodriguez"},{"id":"nm5665374","title":"","description":"Actor, Di ingon &#x27;nato","name":"Tom"},{"id":"nm1303561","title":"","description":"Actor, Xuxa Requebra","name":"Tom"},{"id":"nm2137598","title":"","description":"Actor, Do My Own Thing","name":"Tom"},{"id":"nm2273149","title":"","description":"Actor, Pat Gets a Cat","name":"Tom"},{"id":"nm3363863","title":"","description":"Self, KKK: Inside American Terror","name":"Tom"},{"id":"nm0608846","title":"","description":"Self, Episode dated 15 November 1967","name":"Oswald Mosley"},{"id":"nm5515369","title":"","description":"Actor, Three Many Weddings","name":"Carlos Noriega"},{"id":"nm0662664","title":"","description":"Actor, Never Leave Nevada","name":"Thomas C. Parker"},{"id":"nm0866283","title":"","description":"Self, Faye","name":"Tom"},{"id":"nm5631597","title":"","description":"Actor, U","name":"Tom"},{"id":"nm5856958","title":"","description":"Miscellaneous Crew, Gaiapolis","name":"Tom"},{"id":"nm5943445","title":"","description":"Self, Bush Pilots","name":"Tom"},{"id":"nm4430538","title":"","description":"Self, Episode #1.8","name":"Tom"},{"id":"nm2734582","title":"","description":"Self, Tots TV Australian Adventure","name":"Tom"},{"id":"nm3182552","title":"","description":"Self, Hot Chocolate","name":"Tom"},{"id":"nm3662381","title":"","description":"Self, 9/11 No More Pita Bread in the Lunch Bag","name":"Tom"},{"id":"nm3778536","title":"","description":"Self, Episode #1.1","name":"Tom"},{"id":"nm5708084","title":"","description":"Self, Last Round","name":"Tom Wise"},{"id":"nm1870014","title":"","description":"Director, Lover from Beyond the Grave","name":"Thomas Iuso"},{"id":"nm3239763","title":"","description":"Thanks, Zacharia Farted","name":"Tom Pitt"},{"id":"nm4304684","title":"","description":"Self, Dana Carvey/Linkin Park","name":"Thomas Carvey"},{"id":"nm5065424","title":"","description":"Actor, American Saint","name":"Thomas R. Peters Jr."},{"id":"nm2963568","title":"","description":"Self, On the Couch: Toronto","name":"Tom"},{"id":"nm2126713","title":"","description":"Actor, Inglorious Bitches","name":"Thomas Lee"},{"id":"nm2427252","title":"","description":"Miscellaneous Crew, Sex Evil","name":"Tom"},{"id":"nm1588771","title":"","description":"Actor, Kick Club","name":"Todd Miller"},{"id":"nm2407563","title":"","description":"Actor, World Soccer Orgy: Part 1","name":"Tim Tailor"},{"id":"nm1697195","title":"","description":"Actor, Best of 21Sextury 2005","name":"Tom"},{"id":"nm1047400","title":"","description":"Actor, Lollipops 16","name":"Tom"},{"id":"nm2640808","title":"","description":"Actor, Piss-Party","name":"Tom"},{"id":"nm2007530","title":"","description":"Actor, Hard &#x26; Ready","name":"Tomas Patrik"},{"id":"nm1162304","title":"","description":"Actor, Glamour","name":"Tom"},{"id":"nm1790759","title":"","description":"Actor, Gay Weekend 6","name":"Tom"},{"id":"nm2691088","title":"","description":"Actor, Private Specials 25: Bisexual Clinic","name":"Tom"},{"id":"nm2768376","title":"","description":"Self, Sportin&#x27; Wood","name":"Tom"},{"id":"nm3039343","title":"","description":"Actor, Argentinean Auditions 1","name":"Tom"},{"id":"nm2828677","title":"","description":"Actor, Cinema... That&#x27;s Live","name":"Leo Pohl"},{"id":"nm1766224","title":"","description":"Actor, Paid on Demand","name":"Tom"},{"id":"nm5889920","title":"","description":"Director, P","name":"Tom"},{"id":"nm1831492","title":"","description":"Actor, Teeny Exzesse 53 - Peep","name":"Tom"},{"id":"nm4097660","title":"","description":"Actor, Aged to Perfection 3","name":"Tom"},{"id":"nm2839379","title":"","description":"Actor, Heavy Hung","name":"Tom"},{"id":"nm2912309","title":"","description":"Actor, Aaron&#x27;s Thai-Boy Circle Jerk","name":"Tom"},{"id":"nm3947421","title":"","description":"Self, Le couple et le sexe: Du d&#xE9;sir au plaisir","name":"Tom"},{"id":"nm3824872","title":"","description":"Make-Up Department, V Dreams Vol. 3","name":"Tom"},{"id":"nm5600706","title":"","description":"Uncategorised","name":"Tom"},{"id":"nm5607296","title":"","description":"Uncategorised","name":"Tom"},{"id":"nm4769962","title":"","description":"Uncategorised","name":"Tom"}],"name_popular":[{"id":"nm0001000","title":"","description":"Actor, The Big Lebowski: A XXX Parody","name":"Tom Byron"},{"id":"nm0000169","title":"","description":"Actor, No Country for Old Men","name":"Tommy Lee Jones"},{"id":"nm0565336","title":"","description":"Actor, 2012","name":"Thomas McCarthy"},{"id":"nm0004959","title":"","description":"Actor, Eyes Wide Shut","name":"Thomas Gibson"},{"id":"nm0215281","title":"","description":"Actor, A Nightmare on Elm Street","name":"Thomas Dekker"},{"id":"nm0005875","title":"","description":"Cinematographer, The Usual Suspects","name":"Newton Thomas Sigel"}]}'

        when:

        String result = imdbRequestService.limitJsonToActors(input)

        then:

        !result.contains('Soundtrack,')
        !result.contains('Writer,')

    }

}
