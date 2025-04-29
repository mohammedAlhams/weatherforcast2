package com.example.greenenergyapp

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteDatabase

// User entity with no circular dependency
@Entity(
    tableName = "user",
    indices = [Index(value = ["email"], unique = true)]
)
data class User(
    @PrimaryKey(autoGenerate = true) val userId: Int = 0,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "password") val password: String, // Should be hashed in business logic
    @ColumnInfo(name = "monthly_energy_consumption") val monthlyEnergyConsumption: Float?,
    @ColumnInfo(name = "monthly_bill_amount") val monthlyBillAmount: Float?

    // Removed locationId to fix circular dependency
)

// Location with proper foreign key and index
@Entity(
    tableName = "location",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("user_id")]
)
data class Location(
    @PrimaryKey(autoGenerate = true) val locationId: Int = 0,
    @ColumnInfo(name = "latitude") val latitude: String,
    @ColumnInfo(name = "longitude") val longitude: String,
    @ColumnInfo(name = "available_area") val availableArea: Float,
    @ColumnInfo(name = "area_type") val areaType: String, // Consider using TypeConverter for enum
    @ColumnInfo(name = "user_id") val userId: Int
)

// Appliance management with proper foreign key and index
@Entity(
    tableName = "appliance_management",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("user_id")]
)
data class ApplianceManagement(
    @PrimaryKey(autoGenerate = true) val applianceId: Int = 0,
    @ColumnInfo(name = "appliance_type") val applianceType: String,
    @ColumnInfo(name = "usage_frequency") val usageFrequency: Float,
    @ColumnInfo(name = "purchase_year") val purchaseYear: Int, // Fixed to Int instead of Float
    @ColumnInfo(name = "user_id") val userId: Int
)

// Weather data with proper foreign key and index
@Entity(
    tableName = "weather_data",
    foreignKeys = [
        ForeignKey(
            entity = Location::class,
            parentColumns = ["locationId"],
            childColumns = ["location_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("location_id")]
)
data class WeatherData(
    @PrimaryKey(autoGenerate = true) val weatherDataId: Int = 0,
    @ColumnInfo(name = "location_id") val locationId: Int,
    @ColumnInfo(name = "solar_irradiance") val solarIrradiance: Double,
    @ColumnInfo(name = "wind_speed") val windSpeed: Double,
    @ColumnInfo(name = "peak_sun_hours") val peakSunHours : Double,
    @ColumnInfo(name = "air_density") val airDensity : Double,
    @ColumnInfo(name = "optimal_panel_angle") val optimalPanelAngle : Double
)

// Energy system with proper foreign key and index
@Entity(
    tableName = "energy_system",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("user_id")]
)
data class EnergySystem(
    @PrimaryKey(autoGenerate = true) val systemId: Int = 0,
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "chosen_type") val chosenType: String, // Consider using TypeConverter for enum: SOLAR, WIND, HYBRID
    @ColumnInfo(name = "is_on_grid") val isOnGrid: Boolean,
    @ColumnInfo(name = "storage_capacity") val storageCapacity: Float,
    @ColumnInfo(name = "regulations_passed") val regulationsPassed: Boolean
)

// Solar system with proper foreign key and index
@Entity(
    tableName = "solar_system",
    foreignKeys = [
        ForeignKey(
            entity = EnergySystem::class,
            parentColumns = ["systemId"],
            childColumns = ["system_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("system_id")]
)
data class SolarSystem(
    @PrimaryKey(autoGenerate = true) val solarSystemId: Int = 0,
    @ColumnInfo(name = "system_id") val systemId: Int,
    @ColumnInfo(name = "panel_type") val panelType: String,
    @ColumnInfo(name = "panel_count") val panelCount: Int,
    @ColumnInfo(name = "orientation") val orientation: String,
    @ColumnInfo(name = "calculated_output") val calculatedOutput: Float
)

// Wind system with proper foreign key and index
@Entity(
    tableName = "wind_system",
    foreignKeys = [
        ForeignKey(
            entity = EnergySystem::class,
            parentColumns = ["systemId"],
            childColumns = ["system_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("system_id")]
)
data class WindSystem(
    @PrimaryKey(autoGenerate = true) val windSystemId: Int = 0,
    @ColumnInfo(name = "system_id") val systemId: Int,
    @ColumnInfo(name = "turbine_type") val turbineType: String,
    @ColumnInfo(name = "turbine_count") val turbineCount: Int,
    @ColumnInfo(name = "tower_height") val towerHeight: Float,
    @ColumnInfo(name = "calculated_output") val calculatedOutput: Float
)

// Hybrid system with proper foreign keys and indices
@Entity(
    tableName = "hybrid_system",
    foreignKeys = [
        ForeignKey(
            entity = EnergySystem::class,
            parentColumns = ["systemId"],
            childColumns = ["system_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = WindSystem::class,
            parentColumns = ["windSystemId"],
            childColumns = ["wind_system_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SolarSystem::class,
            parentColumns = ["solarSystemId"],
            childColumns = ["solar_system_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("system_id"),
        Index("wind_system_id"),
        Index("solar_system_id")
    ]
)
data class HybridSystem(
    @PrimaryKey(autoGenerate = true) val hybridSystemId: Int = 0,
    @ColumnInfo(name = "system_id") val systemId: Int,
    @ColumnInfo(name = "wind_system_id") val windSystemId: Int,
    @ColumnInfo(name = "solar_system_id") val solarSystemId: Int,
    @ColumnInfo(name = "calculated_output") val calculatedOutput: Float
)

// Financial analysis with proper foreign keys and indices
@Entity(
    tableName = "financial_analysis",
    foreignKeys = [
        ForeignKey(
            entity = EnergySystem::class,
            parentColumns = ["systemId"],
            childColumns = ["system_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("system_id"),
        Index("user_id")
    ]
)
data class FinancialAnalysis(
    @PrimaryKey(autoGenerate = true) val financialAnalysisId: Int = 0,
    @ColumnInfo(name = "system_id") val systemId: Int,
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "initial_costs") val initialCosts: Float,
    @ColumnInfo(name = "savings") val savings: Float,
    @ColumnInfo(name = "npv") val npv: Float?,
    @ColumnInfo(name = "mirr") val mirr: Float?,
    @ColumnInfo(name = "payback_period") val paybackPeriod: Float
)

// DAOs with consistent suspend functions
@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User): Long

    @Delete
    suspend fun deleteUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT * FROM user")
    fun getAllUsers(): LiveData<List<User>>

    @Query("SELECT * FROM user WHERE userId = :userId")
    suspend fun getUserById(userId: Int): User?

    @Query("SELECT * FROM user WHERE email = :email AND password = :password")
    suspend fun getUserByEmailAndPassword(email: String, password: String): User?

    @Query("SELECT EXISTS(SELECT 1 FROM user WHERE email = :email)")
    suspend fun doesEmailExist(email: String): Boolean

    @Query("SELECT password FROM user WHERE email = :email LIMIT 1")
    suspend fun getPasswordByEmail(email: String): String?
}

@Dao
interface LocationDAO {
    @Insert
    suspend fun insertLocation(location: Location): Long

    @Update
    suspend fun updateLocation(location: Location)

    @Delete
    suspend fun deleteLocation(location: Location)

    @Query("SELECT * FROM location WHERE locationId = :locationId")
    suspend fun getLocationById(locationId: Int): Location?

    @Query("SELECT * FROM location WHERE user_id = :userId")
    fun getLocationsByUserId(userId: Int): LiveData<List<Location>>

    @Query("SELECT * FROM location")
    suspend fun getAllLocationsDirectList(): List<Location>

    @Query("SELECT * FROM location WHERE user_id = :userId LIMIT 1")
    suspend fun getSingleLocationByUserId(userId: Int): Location?
}

@Dao
interface ApplianceManagementDAO {
    @Insert
    suspend fun insertApplianceManagement(applianceManagement: ApplianceManagement): Long

    @Update
    suspend fun updateApplianceManagement(applianceManagement: ApplianceManagement)

    @Delete
    suspend fun deleteApplianceManagement(applianceManagement: ApplianceManagement)

    @Query("SELECT * FROM appliance_management WHERE applianceId = :applianceId")
    suspend fun getApplianceManagementById(applianceId: Int): ApplianceManagement?

    @Query("SELECT * FROM appliance_management WHERE user_id = :userId")
    fun getAppliancesByUserId(userId: Int): LiveData<List<ApplianceManagement>>

    @Query("SELECT * FROM appliance_management")
    fun getAllApplianceManagements(): LiveData<List<ApplianceManagement>>
}

@Dao
interface WeatherDataDAO {
    @Insert
    suspend fun insertWeatherData(weatherData: WeatherData): Long

    @Update
    suspend fun updateWeatherData(weatherData: WeatherData)

    @Delete
    suspend fun deleteWeatherData(weatherData: WeatherData)

    @Query("SELECT * FROM weather_data WHERE weatherDataId = :weatherDataId")
    suspend fun getWeatherDataById(weatherDataId: Int): WeatherData?

    @Query("SELECT * FROM weather_data WHERE location_id = :locationId")
    fun getWeatherDataByLocation(locationId: Int): LiveData<List<WeatherData>>

    @Query("SELECT * FROM weather_data")
    fun getAllWeatherData(): LiveData<List<WeatherData>>

    @Query("SELECT * FROM weather_data WHERE location_id = :locationId")
    suspend fun getWeatherDataByLocationId(locationId: Int): List<WeatherData>

    @Query("SELECT * FROM weather_data WHERE location_id = :locationId LIMIT 1")
    suspend fun getSingleWeatherDataByLocationId(locationId: Int): WeatherData?
}

@Dao
interface EnergySystemDAO {
    @Insert
    suspend fun insertEnergySystem(energySystem: EnergySystem): Long

    @Update
    suspend fun updateEnergySystem(energySystem: EnergySystem)

    @Delete
    suspend fun deleteEnergySystem(energySystem: EnergySystem)

    @Query("SELECT * FROM energy_system WHERE systemId = :systemId")
    suspend fun getEnergySystemById(systemId: Int): EnergySystem?

    @Query("SELECT * FROM energy_system WHERE user_id = :userId")
    fun getEnergySystemsByUserId(userId: Int): LiveData<List<EnergySystem>>

    @Query("SELECT * FROM energy_system")
    fun getAllEnergySystems(): LiveData<List<EnergySystem>>
}

@Dao
interface SolarSystemDAO {
    @Insert
    suspend fun insertSolarSystem(solarSystem: SolarSystem): Long

    @Update
    suspend fun updateSolarSystem(solarSystem: SolarSystem)

    @Delete
    suspend fun deleteSolarSystem(solarSystem: SolarSystem)

    @Query("SELECT * FROM solar_system WHERE solarSystemId = :solarSystemId")
    suspend fun getSolarSystemById(solarSystemId: Int): SolarSystem?

    @Query("SELECT * FROM solar_system WHERE system_id = :systemId")
    suspend fun getSolarSystemByEnergySystemId(systemId: Int): SolarSystem?

    @Query("SELECT * FROM solar_system")
    fun getAllSolarSystems(): LiveData<List<SolarSystem>>
}

@Dao
interface WindSystemDAO {
    @Insert
    suspend fun insertWindSystem(windSystem: WindSystem): Long

    @Update
    suspend fun updateWindSystem(windSystem: WindSystem)

    @Delete
    suspend fun deleteWindSystem(windSystem: WindSystem)

    @Query("SELECT * FROM wind_system WHERE windSystemId = :windSystemId")
    suspend fun getWindSystemById(windSystemId: Int): WindSystem?

    @Query("SELECT * FROM wind_system WHERE system_id = :systemId")
    suspend fun getWindSystemByEnergySystemId(systemId: Int): WindSystem?

    @Query("SELECT * FROM wind_system")
    fun getAllWindSystems(): LiveData<List<WindSystem>>
}

@Dao
interface HybridSystemDAO {
    @Insert
    suspend fun insertHybridSystem(hybridSystem: HybridSystem): Long

    @Update
    suspend fun updateHybridSystem(hybridSystem: HybridSystem)

    @Delete
    suspend fun deleteHybridSystem(hybridSystem: HybridSystem)

    @Query("SELECT * FROM hybrid_system WHERE hybridSystemId = :hybridSystemId")
    suspend fun getHybridSystemById(hybridSystemId: Int): HybridSystem?

    @Query("SELECT * FROM hybrid_system WHERE system_id = :systemId")
    suspend fun getHybridSystemByEnergySystemId(systemId: Int): HybridSystem?

    @Query("SELECT * FROM hybrid_system")
    fun getAllHybridSystems(): LiveData<List<HybridSystem>>
}

@Dao
interface FinancialAnalysisDAO {
    @Insert
    suspend fun insertFinancialAnalysis(financialAnalysis: FinancialAnalysis): Long

    @Update
    suspend fun updateFinancialAnalysis(financialAnalysis: FinancialAnalysis)

    @Delete
    suspend fun deleteFinancialAnalysis(financialAnalysis: FinancialAnalysis)

    @Query("SELECT * FROM financial_analysis WHERE financialAnalysisId = :financialAnalysisId")
    suspend fun getFinancialAnalysisById(financialAnalysisId: Int): FinancialAnalysis?

    @Query("SELECT * FROM financial_analysis WHERE system_id = :systemId")
    suspend fun getFinancialAnalysisBySystemId(systemId: Int): FinancialAnalysis?

    @Query("SELECT * FROM financial_analysis WHERE user_id = :userId")
    fun getFinancialAnalysesByUserId(userId: Int): LiveData<List<FinancialAnalysis>>

    @Query("SELECT * FROM financial_analysis")
    fun getAllFinancialAnalyses(): LiveData<List<FinancialAnalysis>>
}

@Database(
    entities = [
        User::class,
        Location::class,
        ApplianceManagement::class,
        WeatherData::class,
        EnergySystem::class,
        SolarSystem::class,
        WindSystem::class,
        HybridSystem::class,
        FinancialAnalysis::class
    ],
    version = 3
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun locationDao(): LocationDAO
    abstract fun applianceManagementDao(): ApplianceManagementDAO
    abstract fun weatherDataDao(): WeatherDataDAO
    abstract fun energySystemDao(): EnergySystemDAO
    abstract fun solarSystemDao(): SolarSystemDAO
    abstract fun windSystemDao(): WindSystemDAO
    abstract fun hybridSystemDao(): HybridSystemDAO
    abstract fun financialAnalysisDao(): FinancialAnalysisDAO
}

class AppDataBaseRepository private constructor(context: Context) {
    // Singleton instance
    companion object {
        @Volatile
        private var INSTANCE: AppDataBaseRepository? = null

        fun getInstance(context: Context): AppDataBaseRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AppDataBaseRepository(context).also { INSTANCE = it }
            }
        }
    }

    private fun buildDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "app_database.db")
            .fallbackToDestructiveMigration() // This will delete all data when schema changes
            .build()
    }

    private val database = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "app_database.db"
    )
        .fallbackToDestructiveMigration()
        .addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Log the names of all created tables
                val cursor = db.query("SELECT name FROM sqlite_master WHERE type='table'")
                cursor.use {
                    if (it.moveToFirst()) {
                        do {
                            val tableName = it.getString(0)
                            Log.d("Database", "Table created: $tableName")
                        } while (it.moveToNext())
                    }
                }
                // OPTIONAL: Seed the database with initial data if needed
                db.execSQL("INSERT INTO user (email, password, monthly_energy_consumption) VALUES ('test@example.com', 'password123', 50.0)")
                Log.d("Database", "Seeded initial user data")
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                // Ensure foreign keys are enabled
                db.execSQL("PRAGMA foreign_keys = ON")
                Log.d("Database", "Database opened")
            }
        })
        .build()


    fun getUserDAO(): UserDao {
        return database.userDao()
    }

    fun getLocationDAO(): LocationDAO {
        return database.locationDao()
    }

    fun getApplianceManagementDAO(): ApplianceManagementDAO {
        return database.applianceManagementDao()
    }

    fun getWeatherDataDAO(): WeatherDataDAO {
        return database.weatherDataDao()
    }

    fun getEnergySystemDAO(): EnergySystemDAO {
        return database.energySystemDao()
    }

    fun getSolarSystemDAO(): SolarSystemDAO {
        return database.solarSystemDao()
    }

    fun getWindSystemDAO(): WindSystemDAO {
        return database.windSystemDao()
    }

    fun getHybridSystemDAO(): HybridSystemDAO {
        return database.hybridSystemDao()
    }

    fun getFinancialAnalysisDAO(): FinancialAnalysisDAO {
        return database.financialAnalysisDao()
    }
}