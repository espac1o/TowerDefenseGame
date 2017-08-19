using UnityEngine;
using System.Collections.Generic;
using System.Runtime.CompilerServices;

public class UnitScript : MonoBehaviour {



    public float speed;
    public float Speed
    {
        get
        {
            return speed;
        }
        set
        {
            speed = value;
        }
    }

    public float hp;
    public float HP
    {
        get
        {
            return hp;
        }
        set
        {
            hp = value;
        }
    }

    public int damage;
    public int Damage
    {
        get
        {
            return damage;
        }
        set
        {
            damage = value;
        }
    }

    public int reward;
    public int Reward
    {
        get
        {
            return reward;
        }
        set
        {
            reward = value;
        }
    }

    public float currDelay;
    public bool ready;

    public float timeScale;

    public bool wasHere1 = false;
    public bool wasHere2 = false;
    public bool wasHere3 = false;

	//private Dictionary<int, Vector2> route; // general route to nexus, get it from GeneratorScript
    public Queue<Vector2> route;
    public Vector2[] routeTMP;

//	private int currStep; // how much it went (amount of cells)
    private Vector2 currentStep;

    public Vector2 TMP;

    private readonly object syncLostHPLock = new object();
    
    // Use this for initialization
	void Start () {
		//route = GameObject.Find ("GENERATOR").GetComponent<GeneratorScript> ().roadRoute;
        route = new Queue<Vector2>(GameObject.Find("GENERATOR").GetComponent<GeneratorScript>().route);
		//currStep = 0;
        routeTMP = route.ToArray();
        currentStep = route.Dequeue();
        currDelay = 0;
        
	}
	
	// Update is called once per frame
	void FixedUpdate () {
		Move();
        if (!ready)
        {
            if (currDelay > 0)
            {
                currDelay -= Time.deltaTime;
            }
            else
            {
                ready = true;
            }
        }
	}

    /*
    void Move()
    {
        if (currStep + 1 >= route.Count) {
            GameObject.Find("COUNTER").GetComponent<CounterScript>().DamageToNexus(Damage);
            Destroy(gameObject);
            return;
        }
        Vector3 targetedCell = route[currStep + 1];
        TMP = route[currStep + 1];
        Vector2 direction = targetedCell - gameObject.GetComponent<Transform>().position;
        if (direction.magnitude <= 0.3 * GameObject.Find("GENERATOR").GetComponent<GeneratorScript>().CELL_SIZE)
        {
            currStep++;
            Move();
        }
        else
        {
            direction.Normalize();
            gameObject.GetComponent<Rigidbody2D>().velocity = direction * Speed;
            Rotate(direction);
        }
    }
    */

    void Move()
    {
        Vector3 targetedCell = currentStep;
        TMP = currentStep;
        Vector2 direction = targetedCell - gameObject.GetComponent<Transform>().position;
        if (direction.magnitude <= 0.3 * GameObject.Find("GENERATOR").GetComponent<GeneratorScript>().CELL_SIZE)
        {
            if (route.Count != 0)
            {
                currentStep = route.Dequeue();
                Move();
            }
            else
            {
                GameObject.Find("COUNTER").GetComponent<CounterScript>().DamageToNexus(Damage);
                Destroy(gameObject);
                return;
            }
        }
        else
        {
            direction.Normalize();
            gameObject.GetComponent<Rigidbody2D>().velocity = direction * Speed;
            Rotate(direction);
        }
    }

    void Rotate(Vector2 direction)
    {
        gameObject.GetComponent<Rigidbody2D>().MoveRotation(Mathf.Asin(direction.y) * 180 / Mathf.PI);
        if (direction.x < 0) { gameObject.GetComponent<Rigidbody2D>().MoveRotation(180 - 
            Mathf.Asin(direction.y) * 180 / Mathf.PI); }
    }

    public void LostHp(float damage) {
        lock (syncLostHPLock)
        {
            if (HP <= 0) return;
            HP -= damage;
            if (HP <= 0)
            {
                GameObject.Find("COUNTER").GetComponent<CounterScript>().Cash(Reward);
                ApplicationStatistics.frags++;
                Destroy(gameObject);
            }
        }
	}

    void OnDestroy()
    {
        var spawner = GameObject.Find("SPAWNER");
        if (spawner != null)
            spawner.GetComponent<SpawnerScript>().MonstersList.Remove(gameObject);
    }

    void OnTriggerStay2D(Collider2D obj)
    {
        AoeScript AS = obj.GetComponent<AoeScript>();
        wasHere1 = true;
        if (AS != null)
        {
            wasHere2 = true;
            if (ready)
            {
                wasHere3 = true;
                ready = false;
                currDelay = AS.maxDelay;
                LostHp(AS.damage);
            }
        }
    }
}
